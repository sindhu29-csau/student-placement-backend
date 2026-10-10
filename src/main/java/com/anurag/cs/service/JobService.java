package com.anurag.cs.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.anurag.cs.dto.JobDTO;
import com.anurag.cs.entity.Company;
import com.anurag.cs.entity.Job;
import com.anurag.cs.repository.CompanyRepository;
import com.anurag.cs.repository.JobRepository;

@Service
public class JobService {

    private final JobRepository repo;
    private final CompanyRepository companyRepo;

    public JobService(
            JobRepository repo,
            CompanyRepository companyRepo) {

        this.repo = repo;
        this.companyRepo = companyRepo;
    }

    public ResponseEntity<?> createJob(JobDTO dto) {

        Company company = companyRepo
                .findById(dto.getCompanyId())
                .orElse(null);

        if (company == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Company not found");
        }

        Job job = new Job();

        job.setCompany(company);

        job.setTitle(dto.getTitle().trim());

        job.setMinCgpa(dto.getMinCgpa());

        job.setEligibleDepartment(
                dto.getEligibleDepartment()
        );

        if (dto.getRequiredSkills() != null) {
            job.setRequiredSkills(
                    dto.getRequiredSkills().trim()
            );
        }

        if (dto.getApplicationDeadline() != null
                && !dto.getApplicationDeadline().isBlank()) {

            try {

                job.setApplicationDeadline(
                        LocalDate.parse(
                                dto.getApplicationDeadline()
                        )
                );

            } catch (DateTimeParseException ex) {

                return ResponseEntity.badRequest()
                        .body(
                                "Application deadline must be in yyyy-MM-dd format"
                        );
            }
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repo.save(job));
    }

    public List<Job> getAll() {
        return repo.findAll();
    }

    public ResponseEntity<?> getById(Long id) {

        return repo.findById(id)
                .<ResponseEntity<?>>map(
                        ResponseEntity::ok
                )
                .orElseGet(
                        () -> ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body("Job not found")
                );
    }
}