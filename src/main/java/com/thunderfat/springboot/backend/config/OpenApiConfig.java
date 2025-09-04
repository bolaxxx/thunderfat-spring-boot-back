package com.thunderfat.springboot.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;

/**
 * OpenAPI 3.0 configuration for ThunderFat API documentation
 * Configures Swagger UI with authentication and comprehensive API info
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI thunderFatOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ThunderFat Nutrition Management API")
                .description("REST API for nutrition and dietitian management system. " +
                           "Manages relationships between nutritionists and patients, " +
                           "including diet plans, medical measurements, appointments, and nutrition tracking.")
                .version("v1.0")
                .contact(new Contact()
                    .name("ThunderFat Development Team")
                    .email("dev@thunderfat.com")
                    .url("https://github.com/bolaxxx/thunderfat-spring-boot-back"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .addSecurityItem(new SecurityRequirement().addList("JWT"))
            .addSecurityItem(new SecurityRequirement().addList("OAuth2"))
            .components(new Components()
                .addSecuritySchemes("JWT", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtained from /api/auth/login"))
                .addSecuritySchemes("OAuth2", new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .description("OAuth2 flow for client applications")
                    .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                        .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                            .authorizationUrl("/oauth2/authorize")
                            .tokenUrl("/oauth2/token")
                            .refreshUrl("/oauth2/token")))));
    }
}
