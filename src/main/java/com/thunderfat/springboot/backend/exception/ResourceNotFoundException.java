package com.thunderfat.springboot.backend.exception;

/**
 * Exception for when a requested resource is not found
 * Used for 404 Not Found scenarios
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
