package com.anurag.cs.dto;

public class LoginResponseDTO {
    private String token;
    private Long id;
    private String name;
    private String email;
    private String role;
    private Double cgpa;
    private String department;

    public LoginResponseDTO(String token, Long id, String name, String email, String role, Double cgpa, String department) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.cgpa = cgpa;
        this.department = department;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
