package com.thunderfat.springboot.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Test Security Configuration that disables security for integration tests.
 * This configuration allows all requests without authentication to simplify testing.
 * Automatically activated for all test contexts.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    /**
     * Test security filter chain that permits all requests.
     * This is only active during tests to avoid authentication complexity.
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Allow all requests during testing
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // Allow H2 console

        return http.build();
    }

    /**
     * In-memory user details for testing
     */
    @Bean
    @Primary
    public UserDetailsService testUserDetailsService() {
        UserDetails nutricionista = User.builder()
            .username("test.nutritionist@example.com")
            .password(testPasswordEncoder().encode("testpass"))
            .authorities("ROLE_NUTRICIONISTA")
            .build();

        UserDetails admin = User.builder()
            .username("test.admin@example.com") 
            .password(testPasswordEncoder().encode("testpass"))
            .authorities("ROLE_ADMIN")
            .build();

        UserDetails paciente = User.builder()
            .username("test.patient@example.com")
            .password(testPasswordEncoder().encode("testpass"))
            .authorities("ROLE_PACIENTE")
            .build();

        return new InMemoryUserDetailsManager(nutricionista, admin, paciente);
    }

    /**
     * Fast password encoder for tests
     */
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4); // Lower strength for faster tests
    }
}
