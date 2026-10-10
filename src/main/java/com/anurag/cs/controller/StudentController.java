package com.anurag.cs.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.anurag.cs.dto.AdminRegisterDTO;
import com.anurag.cs.dto.AdminStudentRegisterDTO;
import com.anurag.cs.dto.ChangePasswordDTO;
import com.anurag.cs.dto.LoginDTO;
import com.anurag.cs.dto.StudentProfileDTO;
import com.anurag.cs.entity.Student;
import com.anurag.cs.service.StudentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/students")
public class StudentController {

    private static final Path RESUME_DIR = Paths.get("uploads", "resumes");

    @Autowired
    private StudentService service;

    @GetMapping("")
    public List<Student> getAllStudents() {
        return service.getAllStudents();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) { return service.login(dto); }

    @PostMapping("/student-login")
    public ResponseEntity<?> studentLogin(@Valid @RequestBody LoginDTO dto) { return service.loginAsStudent(dto); }

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginDTO dto) { return service.loginAsAdmin(dto); }

    @PostMapping("/admin-student")
    public ResponseEntity<?> createStudentByAdmin(@Valid @RequestBody AdminStudentRegisterDTO dto, Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Only administrators can register students");
        try {
            Student student = service.createStudentByAdmin(dto);
            return ResponseEntity.status(201).body(student);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody AdminRegisterDTO dto, Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Only administrators can create another administrator");
        try {
            service.createAdmin(dto);
            return ResponseEntity.status(201).body("Admin account created successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO dto, Authentication authentication) {
        Student currentUser = (Student) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(service.changePassword(currentUser, dto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id, Authentication authentication) {
        Student currentUser = (Student) authentication.getPrincipal();
        if (!isAdmin(authentication) && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).body("You can only view your own profile");
        }
        Student student = service.getStudentById(id);
        return student == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(student);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @Valid @RequestBody StudentProfileDTO dto, Authentication authentication) {
        Student currentUser = (Student) authentication.getPrincipal();
        if (!isAdmin(authentication) && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).body("You can only update your own profile");
        }
        Student student = service.getStudentById(id);
        if (student == null) return ResponseEntity.notFound().build();
        applyProfile(student, dto);
        return ResponseEntity.ok(service.updateStudent(id, student));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<?> uploadResume(@PathVariable Long id, @RequestParam("resume") MultipartFile file, Authentication authentication) {
        Student currentUser = (Student) authentication.getPrincipal();
        if (!isAdmin(authentication) && !currentUser.getId().equals(id)) return ResponseEntity.status(403).body("You can only upload your own resume");
        if (file == null || file.isEmpty()) return ResponseEntity.badRequest().body("Please select a resume PDF");
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest().body("Only PDF resumes are supported");
        }
        if (file.getSize() > 5 * 1024 * 1024) return ResponseEntity.badRequest().body("Resume must be 5 MB or smaller");
        Student student = service.getStudentById(id);
        if (student == null) return ResponseEntity.notFound().build();
        try {
            byte[] fileBytes = file.getBytes();
            if (fileBytes.length < 5
                    || fileBytes[0] != '%'
                    || fileBytes[1] != 'P'
                    || fileBytes[2] != 'D'
                    || fileBytes[3] != 'F'
                    || fileBytes[4] != '-') {
                return ResponseEntity.badRequest().body("The selected file is not a valid PDF");
            }
            Files.createDirectories(RESUME_DIR);
            String safeName = "student-" + id + ".pdf";
            Files.write(RESUME_DIR.resolve(safeName), fileBytes);
            String displayName = Paths.get(originalFilename).getFileName().toString()
                    .replaceAll("[\\r\\n\"]", "_");
            student.setResumeFileName(displayName.isBlank() ? "resume.pdf" : displayName);
            service.updateStudent(id, student);
            return ResponseEntity.ok("Resume uploaded successfully");
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body("Unable to save resume");
        }
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<?> viewResume(@PathVariable Long id, Authentication authentication) {
        if (!isAdmin(authentication) && !((Student) authentication.getPrincipal()).getId().equals(id)) return ResponseEntity.status(403).body("You can only view your own resume");
        Student student = service.getStudentById(id);
        if (student == null) return ResponseEntity.notFound().build();
        Path file = RESUME_DIR.resolve("student-" + id + ".pdf");
        if (!Files.exists(file)) return ResponseEntity.notFound().build();
        try {
            Resource resource = new UrlResource(file.toUri());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (student.getResumeFileName() == null ? "resume.pdf" : student.getResumeFileName()) + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unable to open resume");
        }
    }

    @GetMapping("/{id}/resume/download")
    public ResponseEntity<?> downloadResume(@PathVariable Long id, Authentication authentication) {
        if (!isAdmin(authentication) && !((Student) authentication.getPrincipal()).getId().equals(id)) return ResponseEntity.status(403).body("You can only download your own resume");
        Student student = service.getStudentById(id);
        if (student == null) return ResponseEntity.notFound().build();
        Path file = RESUME_DIR.resolve("student-" + id + ".pdf");
        if (!Files.exists(file)) return ResponseEntity.notFound().build();
        try {
            Resource resource = new UrlResource(file.toUri());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + (student.getResumeFileName() == null ? "resume.pdf" : student.getResumeFileName()) + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unable to download resume");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student, Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Only administrators can update student records");
        Student updated = service.updateStudent(id, student);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id, Authentication authentication) {
        if (!isAdmin(authentication)) return ResponseEntity.status(403).body("Only administrators can delete students");
        String result = service.deleteStudent(id);
        if ("Student deleted successfully".equals(result)) {
            try {
                Files.deleteIfExists(RESUME_DIR.resolve("student-" + id + ".pdf"));
            } catch (IOException ex) {
                // The database deletion has completed; an orphaned resume can be cleaned up later.
            }
            return ResponseEntity.ok(result);
        }
        if ("Student not found".equals(result)) {
            return ResponseEntity.status(404).body(result);
        }
        return ResponseEntity.status(409).body(result);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void applyProfile(Student student, StudentProfileDTO dto) {
        student.setName(dto.getName().trim());
        student.setRollNumber(dto.getRollNumber().trim());
        student.setCgpa(dto.getCgpa());
        student.setDepartment(dto.getDepartment().trim());
        student.setGender(clean(dto.getGender()));
        student.setPhoneNumber(clean(dto.getPhoneNumber()));
        student.setGraduationYear(dto.getGraduationYear());
        student.setSkills(clean(dto.getSkills()));
        student.setCertifications(clean(dto.getCertifications()));
        student.setProjects(clean(dto.getProjects()));
    }

    private String clean(String value) { return value == null ? null : value.trim(); }
}
