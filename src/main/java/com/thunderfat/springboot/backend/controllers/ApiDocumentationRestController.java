package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced API documentation and information controller
 * Provides standardized API information and endpoint discovery
 */
@Tag(name = "API Information", description = "API documentation and discovery endpoints")
@RestController
@RequestMapping("/api")
public class ApiDocumentationRestController {

    @Operation(
        summary = "Get API information",
        description = "Returns comprehensive information about the ThunderFat API including version, features, and capabilities"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "API information retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ManualApiResponseDTO.class))
    )
    @GetMapping("/info")
    public ManualApiResponseDTO<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "ThunderFat Spring Boot Backend");
        info.put("version", "v1.0.0-2025");
        info.put("springBoot", "3.5.4");
        info.put("java", "21");
        info.put("description", "Modern nutrition and dietitian management system with OAuth2 + JWT authentication");
        
        Map<String, Object> features = new HashMap<>();
        features.put("authentication", "Dual OAuth2 + JWT");
        features.put("database", "MySQL 8.0+ with JPA/Hibernate");
        features.put("security", "Spring Security 6.x with Authorization Server 1.3.2");
        features.put("monitoring", "Spring Actuator + Micrometer + Prometheus");
        features.put("documentation", "OpenAPI 3.0 with Swagger UI");
        features.put("architecture", "Layered with DTO pattern and MapStruct");
        
        info.put("features", features);
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("swagger-ui", "/swagger-ui.html");
        endpoints.put("api-docs", "/api-docs");
        endpoints.put("health", "/actuator/health");
        endpoints.put("metrics", "/actuator/metrics");
        
        info.put("documentation", endpoints);
        info.put("timestamp", LocalDateTime.now());
        
        return ManualApiResponseDTO.success(info, "API information retrieved successfully");
    }

    @Operation(
        summary = "Get available endpoints",
        description = "Returns a categorized list of all available API endpoints with descriptions"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Endpoints retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ManualApiResponseDTO.class))
    )
    @GetMapping("/endpoints")
    public ManualApiResponseDTO<Map<String, Object>> getAvailableEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();
        
        // Authentication endpoints
        Map<String, String> auth = new HashMap<>();
        auth.put("POST /api/auth/login", "JWT Authentication login");
        auth.put("POST /api/auth/refresh", "JWT Token refresh");
        auth.put("GET /oauth2/authorize", "OAuth2 Authorization Code flow");
        auth.put("POST /oauth2/token", "OAuth2 Token exchange");
        auth.put("GET /.well-known/jwks.json", "JWT Public Keys");
        auth.put("GET /.well-known/openid_configuration", "OIDC Configuration");
        
        // Patient management (modern endpoints)
        Map<String, String> patients = new HashMap<>();
        patients.put("GET /api/v1/pacientes", "Get all patients");
        patients.put("GET /api/v1/pacientes/{id}", "Get patient by ID");
        patients.put("GET /api/v1/pacientes/nutricionista/{id}", "Get patients by nutritionist");
        patients.put("POST /api/v1/pacientes", "Create new patient");
        patients.put("PUT /api/v1/pacientes/{id}", "Update patient");
        patients.put("DELETE /api/v1/pacientes/{id}", "Delete patient");
        
        // Health and monitoring
        Map<String, String> monitoring = new HashMap<>();
        monitoring.put("GET /api/health/auth", "Authentication system health");
        monitoring.put("GET /actuator/health", "Application health");
        monitoring.put("GET /actuator/metrics", "Application metrics");
        monitoring.put("GET /actuator/info", "Application information");
        monitoring.put("GET /swagger-ui.html", "Interactive API documentation");
        monitoring.put("GET /api-docs", "OpenAPI 3.0 specification");
        
        // API documentation
        Map<String, String> api = new HashMap<>();
        api.put("GET /api/info", "API information and features");
        api.put("GET /api/endpoints", "Available endpoints catalog");
        api.put("GET /api/test", "API health test endpoint");
        
        endpoints.put("authentication", auth);
        endpoints.put("patients", patients);
        endpoints.put("monitoring", monitoring);
        endpoints.put("documentation", api);
        
        return ManualApiResponseDTO.success(endpoints, "Available endpoints retrieved successfully");
    }

    @Operation(
        summary = "Test API health",
        description = "Simple health check endpoint to verify API is running and accessible"
    )
    @ApiResponse(
        responseCode = "200",
        description = "API is healthy and running",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ManualApiResponseDTO.class))
    )
    @GetMapping("/test")
    public ManualApiResponseDTO<Map<String, Object>> testEndpoint() {
        Map<String, Object> test = new HashMap<>();
        test.put("status", "OK");
        test.put("message", "ThunderFat API is running successfully!");
        test.put("timestamp", LocalDateTime.now());
        test.put("phase", "Phase 1 Complete - API Standardization");
        test.put("features", List.of(
            "OpenAPI 3.0 Documentation",
            "Standardized Response Format",
            "Modern Spring Boot 3.5.4",
            "JWT + OAuth2 Authentication",
            "Comprehensive Error Handling",
            "Health & Monitoring Endpoints"
        ));
        
        return ManualApiResponseDTO.success(test, "API health check completed successfully");
    }
}
