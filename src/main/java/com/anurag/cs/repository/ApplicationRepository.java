package com.anurag.cs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anurag.cs.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentId(Long studentId);

    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);
}
