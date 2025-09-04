package com.thunderfat.springboot.backend.exception;

/**
 * Exception for business rule violations
 * Used when business logic validation fails
 */
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
