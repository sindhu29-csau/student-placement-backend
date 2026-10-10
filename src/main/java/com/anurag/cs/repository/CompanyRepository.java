package com.anurag.cs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anurag.cs.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
