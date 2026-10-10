package com.anurag.cs.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.anurag.cs.entity.Student;
import com.anurag.cs.repository.StudentRepository;
import com.anurag.cs.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final StudentRepository studentRepository;

    public JwtAuthenticationFilter(JwtService jwtService, StudentRepository studentRepository) {
        this.jwtService = jwtService;
        this.studentRepository = studentRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String token = authHeader.substring(7);

            try {
                String email = jwtService.extractUsername(token);
                Student student = studentRepository.findByEmail(email);

                if (student != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    student,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + student.getRole().name()))
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {
                // Invalid or expired JWT: continue without authentication.
                // Spring Security will reject protected endpoints with 401/403.
            }
        }

        filterChain.doFilter(request, response);
    }
}
