package com.thunderfat.springboot.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;

/**
 * OpenAPI 3.1 configuration for ThunderFat API documentation
 * Implements contract-first development standards with versioned API paths
 * Configures JWT authentication and comprehensive API documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI thunderFatOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ThunderFat Nutrition Management API")
                .description("""
                    REST API for comprehensive nutrition and dietitian management system.
                    
                    ## Features
                    - Nutritionist and patient relationship management
                    - Diet plan creation and tracking
                    - Medical measurements and progress monitoring
                    - Appointment scheduling and management
                    - Real-time chat communication
                    - Nutrition tracking and food database
                    - Billing and invoicing (Spanish market)
                    
                    ## Authentication
                    This API uses JWT Bearer tokens for authentication. Obtain a token by calling the /auth/login endpoint.
                    
                    ## Versioning
                    This API follows semantic versioning. The current version is v1.
                    Breaking changes will introduce v2 while maintaining v1 compatibility.
                    
                    ## Rate Limiting
                    API calls are rate-limited. Check response headers for current limits.
                    
                    ## Error Handling
                    All errors follow a standardized format with correlation IDs for tracing.
                    """)
                .version("v1.0.0")
                .contact(new Contact()
                    .name("ThunderFat Development Team")
                    .email("dev@thunderfat.com")
                    .url("https://github.com/bolaxxx/thunderfat-spring-boot-back"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            
            // Add servers
            .addServersItem(new Server()
                .url("http://localhost:8080")
                .description("Development server"))
            .addServersItem(new Server()
                .url("https://api.thunderfat.com")
                .description("Production server"))
            
            // Add security requirements
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            
            // Configure security schemes and components
            .components(new Components()
                // JWT Bearer Authentication
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtained from /auth/login endpoint. " +
                               "Include as: Authorization: Bearer <token>"))
                
                // OAuth2 for future client applications
                .addSecuritySchemes("OAuth2", new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .description("OAuth2 Authorization Code flow for client applications")
                    .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                        .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                            .authorizationUrl("/oauth2/authorize")
                            .tokenUrl("/oauth2/token")
                            .refreshUrl("/oauth2/token")
                            .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                .addString("read", "Read access to resources")
                                .addString("write", "Write access to resources")
                                .addString("admin", "Administrative access"))))));
    }
}
