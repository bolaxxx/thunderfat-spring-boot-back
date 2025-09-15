package com.thunderfat.springboot.backend.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Test configuration for OpenAPI documentation tests
 * Excludes authentication-related components to avoid dependency issues
 */
@TestConfiguration
@ComponentScan(
    basePackages = "com.thunderfat.springboot.backend",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.thunderfat.springboot.backend.auth.AuthenticationController.class,
                com.thunderfat.springboot.backend.auth.SpringSecurityConfig.class
            }
        )
    }
)
public class OpenApiTestConfiguration {
}
