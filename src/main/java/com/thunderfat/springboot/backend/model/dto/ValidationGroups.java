package com.thunderfat.springboot.backend.model.dto;

/**
 * Validation groups for controlling when different validations apply
 * Follows Spring Boot 2025 best practices for validation organization
 */
public interface ValidationGroups {
    
    /**
     * Validation group for entity creation operations
     */
    interface Create {}
    
    /**
     * Validation group for entity update operations  
     */
    interface Update {}
    
    /**
     * Validation group for login operations
     */
    interface Login {}
    
    /**
     * Validation group for password change operations
     */
    interface PasswordChange {}
    
    /**
     * Validation group for admin-only operations
     */
    interface AdminOnly {}
}
