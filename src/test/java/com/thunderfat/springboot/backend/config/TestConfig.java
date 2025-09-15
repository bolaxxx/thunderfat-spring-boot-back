package com.thunderfat.springboot.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.thunderfat.springboot.backend.auth.JwtService;

/**
 * Test configuration for ThunderFat application.
 * Provides mock beans and simplified configurations for testing.
 * 
 * @author ThunderFat Test Team
 * @since Spring Boot 3.5.4
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * Password encoder for test data
     */
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4); // Lower strength for faster tests
    }

    /**
     * Authentication Manager for test environment
     * Using a simple mock that accepts any authentication
     */
    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            authentication.setAuthenticated(true);
            return authentication;
        };
    }

    /**
     * RestTemplate bean for test environment
     */
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Mock JwtService for test environment
     */
    @Bean
    @Primary
    public JwtService jwtService() {
        return org.mockito.Mockito.mock(JwtService.class);
    }

    /**
     * Test properties configuration
     */
    @Bean
    public TestProperties testProperties() {
        return new TestProperties();
    }

    /**
     * Test properties holder
     */
    public static class TestProperties {
        private Performance performance = new Performance();
        
        public Performance getPerformance() {
            return performance;
        }
        
        public static class Performance {
            private boolean enabled = true;
            private long maxResponseTime = 1000L;
            private int concurrentUsers = 10;
            
            // Getters and setters
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }
            
            public long getMaxResponseTime() { return maxResponseTime; }
            public void setMaxResponseTime(long maxResponseTime) { this.maxResponseTime = maxResponseTime; }
            
            public int getConcurrentUsers() { return concurrentUsers; }
            public void setConcurrentUsers(int concurrentUsers) { this.concurrentUsers = concurrentUsers; }
        }
    }
}
