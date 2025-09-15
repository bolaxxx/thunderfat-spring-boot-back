package com.thunderfat.springboot.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Standardized error response DTO following API contract guidelines
 * Provides uniform error structure across all endpoints
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standardized error response")
public class ApiErrorDTO {
    
    @Schema(description = "Error code identifier", example = "VALIDATION_ERROR")
    private String code;
    
    @Schema(description = "Human-readable error message", example = "Validation failed for request")
    private String message;
    
    @Schema(description = "Additional error details or context")
    private Object details;
    
    @Schema(description = "Request path where error occurred", example = "/api/v1/patients")
    private String path;
    
    @Schema(description = "Error timestamp in ISO-8601 format")
    private Instant timestamp;
    
    @Schema(description = "Correlation ID for tracing", example = "abc123def456")
    private String correlationId;
    
    // Factory methods for common error types
    public static ApiErrorDTO validation(String message, Object details, String path, String correlationId) {
        return ApiErrorDTO.builder()
            .code("VALIDATION_ERROR")
            .message(message)
            .details(details)
            .path(path)
            .timestamp(Instant.now())
            .correlationId(correlationId)
            .build();
    }
    
    public static ApiErrorDTO notFound(String resource, String identifier, String path, String correlationId) {
        return ApiErrorDTO.builder()
            .code("RESOURCE_NOT_FOUND")
            .message(String.format("%s with identifier '%s' not found", resource, identifier))
            .path(path)
            .timestamp(Instant.now())
            .correlationId(correlationId)
            .build();
    }
    
    public static ApiErrorDTO unauthorized(String message, String path, String correlationId) {
        return ApiErrorDTO.builder()
            .code("UNAUTHORIZED")
            .message(message != null ? message : "Authentication required")
            .path(path)
            .timestamp(Instant.now())
            .correlationId(correlationId)
            .build();
    }
    
    public static ApiErrorDTO forbidden(String message, String path, String correlationId) {
        return ApiErrorDTO.builder()
            .code("ACCESS_FORBIDDEN")
            .message(message != null ? message : "Access denied")
            .path(path)
            .timestamp(Instant.now())
            .correlationId(correlationId)
            .build();
    }
    
    public static ApiErrorDTO conflict(String message, String path, String correlationId) {
        return ApiErrorDTO.builder()
            .code("RESOURCE_CONFLICT")
            .message(message)
            .path(path)
            .timestamp(Instant.now())
            .correlationId(correlationId)
            .build();
    }
    
    public static ApiErrorDTO internal(String message, String path, String correlationId) {
        return ApiErrorDTO.builder()
            .code("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred")
            .path(path)
            .timestamp(Instant.now())
            .correlationId(correlationId)
            .build();
    }
}
