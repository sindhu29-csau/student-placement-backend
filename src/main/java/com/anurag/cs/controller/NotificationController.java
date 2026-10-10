package com.anurag.cs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anurag.cs.entity.Notification;
import com.anurag.cs.entity.Student;
import com.anurag.cs.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentNotifications(
            @PathVariable Long studentId,
            Authentication authentication) {

        Student currentUser = (Student) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !currentUser.getId().equals(studentId)) {
            return ResponseEntity.status(403)
                    .body("You can only view your own notifications");
        }

        List<Notification> notifications = service.getStudentNotifications(studentId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        Student currentUser = (Student) authentication.getPrincipal();
        boolean updated = service.markAsRead(id, currentUser.getId());

        if (!updated) {
            return ResponseEntity.status(404)
                    .body("Notification not found");
        }

        return ResponseEntity.ok("Notification marked as read");
    }
}
