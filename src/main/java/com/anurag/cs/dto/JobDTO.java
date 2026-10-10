package com.anurag.cs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class JobDTO {

    @NotNull(message = "Company id is required")
    private Long companyId;

    @NotBlank(message = "Title is required")
    private String title;

    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be below 0")
    @DecimalMax(value = "10.0", message = "Minimum CGPA cannot exceed 10")
    private double minCgpa;

    private String eligibleDepartment;

    private String requiredSkills;

    private String applicationDeadline;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public String getEligibleDepartment() {
        return eligibleDepartment;
    }

    public void setEligibleDepartment(String eligibleDepartment) {
        this.eligibleDepartment = eligibleDepartment;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }
}