package com.thunderfat.springboot.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Business validation exception for domain-specific validation failures.
 * This exception is thrown when business rules are violated.
 * 
 * @author ThunderFat Team
 * @since Spring Boot 2025 modernization
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
