package com.anurag.cs.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.anurag.cs.filter.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors
                .configurationSource(corsConfigurationSource())
            )

            .authorizeHttpRequests(auth -> auth

                // Allow browser preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()

                // Public authentication endpoints
                .requestMatchers(
                    "/students/login",
                    "/students/student-login",
                    "/students/admin-login"
                )
                .permitAll()

                // Only an existing ADMIN can create another ADMIN
                .requestMatchers(
                    HttpMethod.POST,
                    "/students/admin"
                )
                .hasRole("ADMIN")

                // Jobs, companies and departments
                .requestMatchers(
                    HttpMethod.GET,
                    "/jobs",
                    "/jobs/**",
                    "/companies",
                    "/companies/**",
                    "/departments",
                    "/departments/**"
                )
                .authenticated()

                // Only ADMIN can view all students
                .requestMatchers(
                    HttpMethod.GET,
                    "/students"
                )
                .hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.POST,
                    "/students/admin-student"
                )
                .hasRole("ADMIN")

                // Students can view their own profile,
                // while ADMIN can view any student
                .requestMatchers(
                    HttpMethod.GET,
                    "/students/{id}"
                )
                .authenticated()

                // Authenticated students and ADMINs can change their own password
                .requestMatchers(
                    HttpMethod.PUT,
                    "/students/change-password"
                )
                .authenticated()

                // Student can update their own profile
                .requestMatchers(
                    HttpMethod.PUT,
                    "/students/*/profile"
                )
                .authenticated()

                // Only ADMIN can perform general student updates
                .requestMatchers(
                    HttpMethod.PUT,
                    "/students/**"
                )
                .hasRole("ADMIN")

                // Only ADMIN can delete students
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/students/**"
                )
                .hasRole("ADMIN")

                // Only ADMIN can create companies, jobs and departments
                .requestMatchers(
                    HttpMethod.POST,
                    "/companies",
                    "/jobs",
                    "/departments"
                )
                .hasRole("ADMIN")

                // Students and ADMIN can view student applications
                .requestMatchers(
                    HttpMethod.GET,
                    "/applications/student/**"
                )
                .hasAnyRole("STUDENT", "ADMIN")

                // Only STUDENT can submit an application
                .requestMatchers(
                    HttpMethod.POST,
                    "/applications"
                )
                .hasRole("STUDENT")

                // Only ADMIN can view all applications
                .requestMatchers(
                    HttpMethod.GET,
                    "/applications"
                )
                .hasRole("ADMIN")

                // Only ADMIN can update application status
                .requestMatchers(
                    HttpMethod.PUT,
                    "/applications/*/status"
                )
                .hasRole("ADMIN")

                // Students and ADMIN can access notifications
                .requestMatchers(
                    HttpMethod.GET,
                    "/notifications/student/**"
                )
                .hasAnyRole("STUDENT", "ADMIN")

                // Students can mark notifications as read
                .requestMatchers(
                    HttpMethod.PUT,
                    "/notifications/*/read"
                )
                .hasRole("STUDENT")

                // Everything else requires authentication
                .anyRequest()
                .authenticated()
            )

            // JWT authentication → stateless session
            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Run JWT filter before Spring's username/password filter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
    List.of(
        "http://localhost:5173",
        "https://student-placement-frontend-o4mg.onrender.com"
    )
);

        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type"
            )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}