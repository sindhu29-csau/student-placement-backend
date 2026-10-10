package com.anurag.cs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anurag.cs.entity.Student;
import com.anurag.cs.enums.Role;

public interface StudentRepository extends JpaRepository<Student, Long> {
      Student findByEmail(String email);
      boolean existsByRole(Role role);
}