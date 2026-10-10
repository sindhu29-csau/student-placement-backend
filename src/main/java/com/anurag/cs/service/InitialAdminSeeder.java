package com.anurag.cs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.anurag.cs.entity.Student;
import com.anurag.cs.enums.Role;
import com.anurag.cs.repository.StudentRepository;

@Component
public class InitialAdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InitialAdminSeeder.class);

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final String initialAdminEmail;
    private final String initialAdminPassword;

    public InitialAdminSeeder(
            StudentRepository studentRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.email:}") String initialAdminEmail,
            @Value("${app.bootstrap-admin.password:}") String initialAdminPassword) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.initialAdminEmail = initialAdminEmail;
        this.initialAdminPassword = initialAdminPassword;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        if (initialAdminEmail == null || initialAdminEmail.isBlank()
                || initialAdminPassword == null || initialAdminPassword.length() < 6) {
            log.warn("No ADMIN account exists. Set INITIAL_ADMIN_EMAIL and INITIAL_ADMIN_PASSWORD to bootstrap the first administrator.");
            return;
        }

        String email = initialAdminEmail.trim().toLowerCase();
        if (studentRepository.findByEmail(email) != null) {
            log.warn("The configured initial administrator email already belongs to an account, but no ADMIN role exists. No account was created.");
            return;
        }

        Student admin = new Student();
        admin.setName("Placement Administrator");
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(initialAdminPassword));
        admin.setRole(Role.ADMIN);
        studentRepository.save(admin);
        log.info("Created the initial administrator account for {}. Change its password after first login.", email);
    }
}
