package com.anurag.cs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anurag.cs.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
