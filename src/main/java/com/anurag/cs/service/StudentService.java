package com.anurag.cs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anurag.cs.dto.AdminRegisterDTO;
import com.anurag.cs.dto.AdminStudentRegisterDTO;
import com.anurag.cs.dto.ChangePasswordDTO;
import com.anurag.cs.dto.LoginDTO;
import com.anurag.cs.dto.LoginResponseDTO;
import com.anurag.cs.entity.Student;
import com.anurag.cs.enums.Role;
import com.anurag.cs.repository.StudentRepository;
import com.anurag.cs.repository.ApplicationRepository;
import com.anurag.cs.repository.NotificationRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repo;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    public Student createStudentByAdmin(AdminStudentRegisterDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();

        if (repo.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Student student = new Student();
        student.setName(dto.getName().trim());
        student.setRollNumber(dto.getRollNumber().trim());
        student.setEmail(email);
        student.setCgpa(dto.getCgpa());
        student.setDepartment(dto.getDepartment().trim());
        student.setGender(clean(dto.getGender()));
        student.setPhoneNumber(clean(dto.getPhoneNumber()));
        student.setGraduationYear(dto.getGraduationYear());
        student.setRole(Role.STUDENT);
        student.setPassword(passwordEncoder.encode(dto.getPassword()));

        return repo.save(student);
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    public Student createAdmin(AdminRegisterDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();

        if (repo.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Student admin = new Student();
        admin.setName(dto.getName().trim());
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRole(Role.ADMIN);

        return repo.save(admin);
    }

    public Student getStudentById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Student updateStudent(Long id, Student student) {
        Student existingStudent = repo.findById(id).orElse(null);

        if (existingStudent == null) {
            return null;
        }

        existingStudent.setName(student.getName());
        existingStudent.setRollNumber(student.getRollNumber());
        existingStudent.setCgpa(student.getCgpa());
        existingStudent.setDepartment(student.getDepartment());
        existingStudent.setGender(student.getGender());
        existingStudent.setPhoneNumber(student.getPhoneNumber());
        existingStudent.setGraduationYear(student.getGraduationYear());
        existingStudent.setSkills(student.getSkills());
        existingStudent.setCertifications(student.getCertifications());
        existingStudent.setProjects(student.getProjects());
        existingStudent.setEmail(student.getEmail());

        // Preserve the uploaded resume filename.
        existingStudent.setResumeFileName(student.getResumeFileName());

        // Avoid rehashing an unchanged BCrypt password.
        if (student.getPassword() != null
                && !student.getPassword().isBlank()
                && !student.getPassword().equals(existingStudent.getPassword())) {
            existingStudent.setPassword(
                    passwordEncoder.encode(student.getPassword()));
        }

        if (student.getRole() != null) {
            existingStudent.setRole(student.getRole());
        }

        return repo.save(existingStudent);
    }

    public String changePassword(Student currentUser, ChangePasswordDTO dto) {
        if (!passwordEncoder.matches(
                dto.getCurrentPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "New password and confirm password do not match");
        }

        if (passwordEncoder.matches(
                dto.getNewPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException(
                    "New password must be different from the current password");
        }

        currentUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repo.save(currentUser);

        return "Password changed successfully";
    }

    @Transactional
    public String deleteStudent(Long id) {
        Student student = repo.findById(id).orElse(null);

        if (student == null) {
            return "Student not found";
        }

        if (student.getRole() != Role.STUDENT) {
            return "Only student accounts can be deleted through Student Management";
        }

        // Delete dependent records before deleting the student.
        applicationRepository.deleteAll(
                applicationRepository.findByStudentId(id));
        applicationRepository.flush();

        notificationRepository.deleteAll(
                notificationRepository.findByStudentIdOrderByCreatedAtDesc(id));
        notificationRepository.flush();

        repo.delete(student);
        repo.flush();

        return "Student deleted successfully";
    }

    public ResponseEntity<?> login(LoginDTO dto) {
        Student existingStudent = authenticate(dto);

        if (existingStudent == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        return createLoginResponse(existingStudent);
    }

    public ResponseEntity<?> loginAsStudent(LoginDTO dto) {
        Student existingStudent = authenticate(dto);

        if (existingStudent == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        if (existingStudent.getRole() != Role.STUDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("This account is not a student account");
        }

        return createLoginResponse(existingStudent);
    }

    public ResponseEntity<?> loginAsAdmin(LoginDTO dto) {
        Student existingStudent = authenticate(dto);

        if (existingStudent == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        if (existingStudent.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("This account is not an administrator account");
        }

        return createLoginResponse(existingStudent);
    }

    private Student authenticate(LoginDTO dto) {
        Student existingStudent =
                repo.findByEmail(dto.getEmail().trim().toLowerCase());

        if (existingStudent == null
                || !passwordEncoder.matches(
                        dto.getPassword(), existingStudent.getPassword())) {
            return null;
        }

        return existingStudent;
    }

    private ResponseEntity<?> createLoginResponse(Student existingStudent) {
        String token =
                jwtService.generateToken(existingStudent.getEmail());

        LoginResponseDTO response = new LoginResponseDTO(
                token,
                existingStudent.getId(),
                existingStudent.getName(),
                existingStudent.getEmail(),
                existingStudent.getRole().name(),
                existingStudent.getCgpa(),
                existingStudent.getDepartment()
        );

        return ResponseEntity.ok(response);
    }
}
