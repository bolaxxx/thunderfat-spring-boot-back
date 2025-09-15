package com.thunderfat.springboot.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test configuration that provides essential beans for @DataJpaTest.
 * @DataJpaTest only loads JPA components, so we need to explicitly provide
 * security beans that some entities or repositories might need.
 * 
 * This configuration avoids importing full security setup to prevent
 * AuthenticationConfiguration dependency issues in test context.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@TestConfiguration
public class TestDataJpaConfig {

    /**
     * Provides PasswordEncoder bean for tests that need security context.
     * This ensures @DataJpaTest can resolve PasswordEncoder dependencies
     * without requiring full Spring Security configuration.
     * 
     * @Primary annotation ensures this bean takes precedence over any
     * other PasswordEncoder beans in the test context.
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
