package com.anurag.cs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anurag.cs.dto.ApplicationDTO;
import com.anurag.cs.dto.ApplicationStatusDTO;
import com.anurag.cs.entity.Application;
import com.anurag.cs.entity.Student;
import com.anurag.cs.service.ApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> apply(@Valid @RequestBody ApplicationDTO dto, Authentication authentication) {
        Student currentUser = (Student) authentication.getPrincipal();
        dto.setStudentId(currentUser.getId());
        return service.apply(dto);
    }

    @GetMapping
    public ResponseEntity<List<Application>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<?> getByStudent(@PathVariable Long id, Authentication authentication) {
        Student currentUser = (Student) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).body("You can only view your own applications");
        }
        return ResponseEntity.ok(service.getByStudent(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusDTO dto) {
        return service.updateStatus(id, dto);
    }
}
