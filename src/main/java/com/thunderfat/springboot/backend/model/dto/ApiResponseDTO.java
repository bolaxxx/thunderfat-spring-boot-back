package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Standardized API response wrapper for consistent response format
 * Provides uniform structure for all API responses including success/error states
 * @param <T> Type of data contained in the response
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {
    
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;
    private String path;
    
    /**
     * Create a successful response with data
     */
    public static <T> ApiResponseDTO<T> success(T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create a successful response with data and custom message
     */
    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponseDTO<T> error(List<String> errors) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponseDTO<T> error(String message, List<String> errors) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
