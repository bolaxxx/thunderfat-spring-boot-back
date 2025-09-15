package com.thunderfat.springboot.backend.repository;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Minimal test configuration for repository tests.
 * Provides only the essential beans needed for JPA repository testing
 * without loading the full application context.
 * 
 * @author ThunderFat Test Team
 * @since Spring Boot 3.5.4
 */
@TestConfiguration
public class RepositoryTestConfig {

    /**
     * Provides a PasswordEncoder bean for tests that might need password encoding.
     * Uses BCryptPasswordEncoder which is the standard for the application.
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
