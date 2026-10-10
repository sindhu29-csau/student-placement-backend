package com.anurag.cs.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AdminStudentRegisterDTO {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Roll number is required")
    private String rollNumber;
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;
    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", message = "CGPA must be at least 0")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10")
    private Double cgpa;
    @NotBlank(message = "Department is required")
    private String department;
    private String gender;
    private String phoneNumber;
    private Integer graduationYear;
    @NotBlank(message = "Temporary password is required")
    @Size(min = 6, message = "Temporary password must contain at least 6 characters")
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
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
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
