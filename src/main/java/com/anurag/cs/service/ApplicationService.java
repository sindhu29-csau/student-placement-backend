package com.anurag.cs.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.anurag.cs.dto.ApplicationDTO;
import com.anurag.cs.dto.ApplicationStatusDTO;
import com.anurag.cs.entity.Application;
import com.anurag.cs.entity.Job;
import com.anurag.cs.entity.Student;
import com.anurag.cs.enums.ApplicationStatus;
import com.anurag.cs.repository.ApplicationRepository;
import com.anurag.cs.repository.JobRepository;
import com.anurag.cs.repository.StudentRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository repo;
    private final StudentRepository studentRepo;
    private final JobRepository jobRepo;
    private final NotificationService notificationService;

    public ApplicationService(
            ApplicationRepository repo,
            StudentRepository studentRepo,
            JobRepository jobRepo,
            NotificationService notificationService) {
        this.repo = repo;
        this.studentRepo = studentRepo;
        this.jobRepo = jobRepo;
        this.notificationService = notificationService;
    }

    public ResponseEntity<?> apply(ApplicationDTO dto) {

        if (repo.existsByStudentIdAndJobId(
                dto.getStudentId(),
                dto.getJobId())) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("You have already applied for this job");
        }

        Student student = studentRepo
                .findById(dto.getStudentId())
                .orElse(null);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found");
        }

        Job job = jobRepo
                .findById(dto.getJobId())
                .orElse(null);

        if (job == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Job not found");
        }

        if (student.getCgpa() == null
                || student.getDepartment() == null
                || student.getDepartment().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Please complete your academic profile before applying");
        }

        boolean meetsCgpa =
                student.getCgpa() >= job.getMinCgpa();

        String requiredDepartment =
                job.getEligibleDepartment() == null
                        ? ""
                        : job.getEligibleDepartment().trim();

        String studentDepartment =
                student.getDepartment()
                        .trim()
                        .toUpperCase();

        String normalizedStudentDepartment =
                studentDepartment.equals("CS")
                        ? "CSE"
                        : studentDepartment;

        boolean meetsDepartment =
                requiredDepartment.isBlank()
                || requiredDepartment.equalsIgnoreCase("all")
                || requiredDepartment.equalsIgnoreCase("all departments")
                || Arrays.stream(requiredDepartment.split(","))
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .map(dept ->
                                dept.equals("CS")
                                        ? "CSE"
                                        : dept)
                        .anyMatch(dept ->
                                dept.equals(
                                        normalizedStudentDepartment));

        if (!meetsCgpa || !meetsDepartment) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not eligible for this job");
        }

        if (job.getApplicationDeadline() != null
                && job.getApplicationDeadline()
                        .isBefore(LocalDate.now())) {

            return ResponseEntity.badRequest()
                    .body("The application deadline for this job has passed");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repo.save(application));
    }

    public List<Application> getAll() {
        return repo.findAll();
    }

    public List<Application> getByStudent(Long studentId) {
        return repo.findByStudentId(studentId);
    }

    public ResponseEntity<?> updateStatus(
            Long id,
            ApplicationStatusDTO dto) {

        Application application =
                repo.findById(id).orElse(null);

        if (application == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Application not found");
        }

        final ApplicationStatus status;

        try {
            status = ApplicationStatus.valueOf(
                    dto.getStatus()
                            .trim()
                            .toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(
                        "Invalid status. Use APPLIED, SHORTLISTED, INTERVIEW, SELECTED or REJECTED"
                    );
        }

        ApplicationStatus oldStatus = application.getStatus();

        application.setStatus(status);
        Application savedApplication = repo.save(application);

        if (oldStatus != status) {
            notificationService.createStatusNotification(
                    application.getStudent(),
                    application.getJob().getTitle(),
                    status.name());
        }

        return ResponseEntity.ok(savedApplication);
    }
}
