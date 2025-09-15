package com.thunderfat.springboot.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * API Configuration for versioning and path matching
 * Implements versioned base path strategy
 */
@Configuration
public class ApiConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        // Configure API versioning
        configurer.addPathPrefix("/api/v1", c -> 
            c.getPackageName().contains("controllers") && 
            !c.getPackageName().contains("auth") &&
            !c.getSimpleName().contains("Auth")
        );
    }
}
