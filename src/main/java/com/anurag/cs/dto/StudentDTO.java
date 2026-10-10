package com.anurag.cs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class StudentDTO {
      @NotBlank(message = "Name is Required")
      private String name;
      @NotBlank(message = "Email is Required")
      @Email(message = "Enter valid email")
      private String email;
      private String role;
      @NotBlank(message = "Password is required")
      private String password;

      public StudentDTO() {

      }

      public void setPassword(String password) {
            this.password = password;
      }

      public String getName() {
            return name;
      }

      public String getEmail() {
            return email;
      }

      public void setName(String name) {
            this.name = name;
      }

      public void setEmail(String email) {
            this.email = email;
      }

      public String getPassword() {
            return password;
      }

      public String getRole() {
            return role;
      }

      public void setRole(String role) {
            this.role = role;
      }

}
