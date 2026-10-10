package com.anurag.cs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.anurag.cs.entity.Notification;
import com.anurag.cs.entity.Student;
import com.anurag.cs.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification createNotification(Student student, String message) {
        Notification notification = new Notification();
        notification.setStudent(student);
        notification.setMessage(message);
        notification.setRead(false);
        return repository.save(notification);
    }

    public Notification createStatusNotification(Student student, String jobTitle, String status) {
        String message = "Your application for " + jobTitle + " has been updated to " + status + ".";
        return createNotification(student, message);
    }

    public List<Notification> getStudentNotifications(Long studentId) {
        return repository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    public boolean markAsRead(Long notificationId, Long studentId) {
        return repository.findById(notificationId)
                .map(notification -> {
                    if (notification.getStudent() == null
                            || !notification.getStudent().getId().equals(studentId)) {
                        return false;
                    }
                    notification.setRead(true);
                    repository.save(notification);
                    return true;
                })
                .orElse(false);
    }
}
