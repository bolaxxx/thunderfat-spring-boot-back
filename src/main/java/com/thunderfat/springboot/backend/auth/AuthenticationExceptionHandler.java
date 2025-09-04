package com.thunderfat.springboot.backend.auth;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice(basePackages = "com.thunderfat.springboot.backend.auth")
public class AuthenticationExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException e, HttpServletRequest request) {
        logger.warn("Bad credentials attempt from IP: {}", getClientIP(request));
        
        Map<String, Object> errorResponse = createErrorResponse(
            "INVALID_CREDENTIALS",
            "Email o contraseña incorrectos",
            HttpStatus.UNAUTHORIZED
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleAccountDisabled(
            DisabledException e, HttpServletRequest request) {
        logger.warn("Account disabled login attempt from IP: {}", getClientIP(request));
        
        Map<String, Object> errorResponse = createErrorResponse(
            "ACCOUNT_DISABLED",
            "La cuenta está deshabilitada. Contacte al administrador",
            HttpStatus.FORBIDDEN
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountLocked(
            LockedException e, HttpServletRequest request) {
        logger.warn("Locked account login attempt from IP: {}", getClientIP(request));
        
        Map<String, Object> errorResponse = createErrorResponse(
            "ACCOUNT_LOCKED",
            "La cuenta está bloqueada. Contacte al administrador",
            HttpStatus.FORBIDDEN
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            UsernameNotFoundException e, HttpServletRequest request) {
        logger.warn("User not found login attempt from IP: {}", getClientIP(request));
        
        // Don't reveal that the user doesn't exist for security reasons
        Map<String, Object> errorResponse = createErrorResponse(
            "INVALID_CREDENTIALS",
            "Email o contraseña incorrectos",
            HttpStatus.UNAUTHORIZED
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(
            JwtException e, HttpServletRequest request) {
        logger.warn("JWT validation error from IP: {}", getClientIP(request));
        
        Map<String, Object> errorResponse = createErrorResponse(
            "INVALID_TOKEN",
            "Token JWT inválido o expirado",
            HttpStatus.UNAUTHORIZED
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException e, HttpServletRequest request) {
        logger.warn("Authentication error from IP: {}", getClientIP(request));
        
        Map<String, Object> errorResponse = createErrorResponse(
            "AUTHENTICATION_FAILED",
            "Error de autenticación",
            HttpStatus.UNAUTHORIZED
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        logger.warn("Validation error in authentication request from IP: {}", getClientIP(request));
        
        Map<String, String> validationErrors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> errorResponse = createErrorResponse(
            "VALIDATION_ERROR",
            "Errores de validación en los datos enviados",
            HttpStatus.BAD_REQUEST
        );
        errorResponse.put("validation_errors", validationErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception e, HttpServletRequest request) {
        logger.error("Unexpected error in authentication from IP: {}", getClientIP(request), e);
        
        Map<String, Object> errorResponse = createErrorResponse(
            "INTERNAL_ERROR",
            "Error interno del servidor",
            HttpStatus.INTERNAL_SERVER_ERROR
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private Map<String, Object> createErrorResponse(String errorCode, String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("status", status.value());
        errorResponse.put("timestamp", LocalDateTime.now());
        return errorResponse;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
