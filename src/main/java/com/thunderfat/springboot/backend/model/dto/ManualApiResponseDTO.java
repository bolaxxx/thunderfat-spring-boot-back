package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Manual implementation of ApiResponseDTO with explicit builder pattern
 * This version works without Lombok to resolve compilation issues
 * 
 * @param <T> Type of data contained in the response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManualApiResponseDTO<T> {
    
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;
    private String path;
    
    // Default constructor
    public ManualApiResponseDTO() {}
    
    // All args constructor
    public ManualApiResponseDTO(boolean success, String message, T data, 
                               List<String> errors, LocalDateTime timestamp, String path) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = timestamp;
        this.path = path;
    }
    
    // Manual Builder Pattern
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
    
    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;
        private List<String> errors;
        private LocalDateTime timestamp;
        private String path;
        
        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }
        
        public Builder<T> errors(List<String> errors) {
            this.errors = errors;
            return this;
        }
        
        public Builder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder<T> path(String path) {
            this.path = path;
            return this;
        }
        
        public ManualApiResponseDTO<T> build() {
            return new ManualApiResponseDTO<>(success, message, data, errors, timestamp, path);
        }
    }
    
    // Static factory methods for common use cases
    public static <T> ManualApiResponseDTO<T> success(T data) {
        return ManualApiResponseDTO.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ManualApiResponseDTO<T> success(T data, String message) {
        return ManualApiResponseDTO.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ManualApiResponseDTO<T> error(String message) {
        return ManualApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ManualApiResponseDTO<T> error(String message, List<String> errors) {
        return ManualApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public String toString() {
        return "ManualApiResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errors=" + errors +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                '}';
    }
}
