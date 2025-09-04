package com.thunderfat.springboot.backend.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

import com.thunderfat.springboot.backend.model.dto.ApiResponseDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Manejador global de excepciones para validaciones y otros errores
 * Enhanced with Spring Boot 2025 best practices
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle business rule violations
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        log.warn("Business exception: {}", ex.getMessage());
        
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * Handle resource not found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Maneja errores de validación de @Valid en el cuerpo de la petición
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        log.warn("Validation error in request {}: {}", request.getDescription(false), errors);
        
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message("Error de validación en los datos enviados")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja errores de validación en form data o query parameters
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleBindErrors(
            BindException ex, WebRequest request) {
        
        List<String> errors = ex.getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        log.warn("Bind error in request {}: {}", request.getDescription(false), errors);
        
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message("Error en los parámetros de la petición")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja violaciones de restricciones a nivel de entidad
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.toList());

        log.warn("Constraint violation in request {}: {}", request.getDescription(false), errors);
        
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message("Error de validación en los datos")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja errores de método HTTP no soportado
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        
        String requestUrl = request.getDescription(false);
        String method = ex.getMethod();
        String[] supportedMethods = ex.getSupportedMethods();
        
        log.warn("Method {} not supported for request {}, supported methods: {}", 
                method, requestUrl, supportedMethods);
        
        // Special handling for authentication endpoints
        String message = "Método HTTP no soportado";
        if (requestUrl.contains("/api/auth/login")) {
            message = "El endpoint de login requiere método POST. " +
                     "Para información sobre los endpoints disponibles, usa GET /api/auth/info";
        } else if (requestUrl.contains("/api/auth")) {
            message = "Los endpoints de autenticación requieren método POST. " +
                     "Para información usa GET /api/auth/info";
        }
        
        if (supportedMethods != null && supportedMethods.length > 0) {
            message += ". Métodos soportados: " + String.join(", ", supportedMethods);
        }
        
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(requestUrl)
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    /**
     * Maneja errores generales no controlados
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGenericError(
            Exception ex, WebRequest request) {
        
        log.error("Unhandled error in request {}: {}", request.getDescription(false), ex.getMessage(), ex);
        
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message("Error interno del servidor")
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String formatFieldError(FieldError fieldError) {
        return "Campo '%s': %s (valor rechazado: '%s')".formatted(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue());
    }

    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        return "Campo '%s': %s (valor rechazado: '%s')".formatted(
                propertyPath,
                violation.getMessage(),
                violation.getInvalidValue());
    }
}
