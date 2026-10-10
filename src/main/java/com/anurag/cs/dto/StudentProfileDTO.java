package com.anurag.cs.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudentProfileDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Roll number is required")
    private String rollNumber;
    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", message = "CGPA must be at least 0")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10")
    private Double cgpa;
    @NotBlank(message = "Department is required")
    private String department;
    private String gender;
    private String phoneNumber;
    private Integer graduationYear;
    private String skills;
    private String certifications;
    private String projects;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }
}
