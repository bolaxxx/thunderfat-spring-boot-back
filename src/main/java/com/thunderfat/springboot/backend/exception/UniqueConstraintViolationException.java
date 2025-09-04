package com.thunderfat.springboot.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a unique constraint violation occurs in the application.
 * Typically used for email, DNI, and other unique fields.
 * Results in a 409 CONFLICT HTTP response.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UniqueConstraintViolationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UniqueConstraintViolationException(String message) {
        super(message);
    }

    public UniqueConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
