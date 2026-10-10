package com.anurag.cs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anurag.cs.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
