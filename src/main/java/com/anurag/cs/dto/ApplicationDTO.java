package com.anurag.cs.dto;

import jakarta.validation.constraints.NotNull;

public class ApplicationDTO {
    // Set by ApplicationController from the authenticated JWT principal.
    private Long studentId;
    @NotNull(message = "Job id is required")
    private Long jobId;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}
