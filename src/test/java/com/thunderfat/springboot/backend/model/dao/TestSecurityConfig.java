package com.thunderfat.springboot.backend.model.dao;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test configuration to provide a PasswordEncoder bean for Spring Security context initialization.
 * Ensures all tests requiring authentication or user creation can run without context errors.
 */
@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
