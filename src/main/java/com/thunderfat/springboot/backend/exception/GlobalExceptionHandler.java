package com.thunderfat.springboot.backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

import com.thunderfat.springboot.backend.model.dto.ApiErrorDTO;
import com.thunderfat.springboot.backend.model.dto.ApiResponseDTO;
import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Global exception handler following API contract guidelines
 * Provides uniform error responses with correlation IDs and proper HTTP status codes
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
     * Enhanced validation error handler with correlation ID and standardized response
     * Handles @Valid validation errors in request bodies
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request,
            WebRequest webRequest) {
        
        String correlationId = generateCorrelationId();
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        log.warn("Validation error [{}]: {}", correlationId, fieldErrors);
        
        // Return contract-compliant error if request path starts with /api/v1
        if (request != null && request.getRequestURI().startsWith("/api/v1")) {
            ApiErrorDTO error = ApiErrorDTO.validation(
                "Validation failed", 
                fieldErrors, 
                getRequestPath(request), 
                correlationId
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        // Fall back to legacy response for backward compatibility
        String errorMessage = "Error de validación en los datos";
        
        return ResponseEntity.badRequest()
                .body(ManualApiResponseDTO.error(errorMessage));
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
    public ResponseEntity<ManualApiResponseDTO<Void>> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.toList());

        log.warn("Constraint violation in request {}: {}", request.getDescription(false), errors);
        
        // Always return generic message for consistency
        String errorMessage = "Error de validación en los datos";
        
        return ResponseEntity.badRequest()
                .body(ManualApiResponseDTO.error(errorMessage));
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

    // Contract-compliant helper methods
    private String generateCorrelationId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String getRequestPath(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : "unknown";
    }

    /**
     * Enhanced resource not found handler with correlation ID
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundEnhanced(
            EntityNotFoundException ex, 
            HttpServletRequest request,
            WebRequest webRequest) {
        
        String correlationId = generateCorrelationId();
        log.warn("Entity not found [{}]: {}", correlationId, ex.getMessage());
        
        // Return contract-compliant error if request path starts with /api/v1
        if (request != null && request.getRequestURI().startsWith("/api/v1")) {
            ApiErrorDTO error = ApiErrorDTO.notFound(
                "Resource", 
                "unknown", 
                getRequestPath(request), 
                correlationId
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        
        // Fall back to legacy response
        ResourceNotFoundException resourceEx = new ResourceNotFoundException("Entity not found");
        return handleResourceNotFoundException(resourceEx, webRequest);
    }

    /**
     * Enhanced authentication error handlers
     */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<?> handleAuthenticationErrorsEnhanced(
            Exception ex, 
            HttpServletRequest request,
            WebRequest webRequest) {
        
        String correlationId = generateCorrelationId();
        log.warn("Authentication error [{}]: {}", correlationId, ex.getMessage());
        
        if (request != null && request.getRequestURI().startsWith("/api/v1")) {
            ApiErrorDTO error = ApiErrorDTO.unauthorized(
                "Invalid credentials", 
                getRequestPath(request), 
                correlationId
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        // Legacy fallback
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message("Invalid credentials")
                .timestamp(LocalDateTime.now())
                .path(webRequest.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Enhanced access denied handler
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedEnhanced(
            AccessDeniedException ex, 
            HttpServletRequest request,
            WebRequest webRequest) {
        
        String correlationId = generateCorrelationId();
        log.warn("Access denied [{}]: {}", correlationId, ex.getMessage());
        
        if (request != null && request.getRequestURI().startsWith("/api/v1")) {
            ApiErrorDTO error = ApiErrorDTO.forbidden(
                "Insufficient permissions", 
                getRequestPath(request), 
                correlationId
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
        
        // Legacy fallback
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message("Access denied")
                .timestamp(LocalDateTime.now())
                .path(webRequest.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Enhanced data integrity violation handler
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationEnhanced(
            DataIntegrityViolationException ex, 
            HttpServletRequest request,
            WebRequest webRequest) {
        
        String correlationId = generateCorrelationId();
        log.warn("Data integrity violation [{}]: {}", correlationId, ex.getMessage());
        
        if (request != null && request.getRequestURI().startsWith("/api/v1")) {
            ApiErrorDTO error = ApiErrorDTO.conflict(
                "Data conflict - resource may already exist or violate constraints", 
                getRequestPath(request), 
                correlationId
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        
        // Legacy fallback
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message("Data conflict occurred")
                .timestamp(LocalDateTime.now())
                .path(webRequest.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Enhanced message not readable handler
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableEnhanced(
            HttpMessageNotReadableException ex, 
            HttpServletRequest request,
            WebRequest webRequest) {
        
        String correlationId = generateCorrelationId();
        log.warn("Message not readable [{}]: {}", correlationId, ex.getMessage());
        
        if (request != null && request.getRequestURI().startsWith("/api/v1")) {
            ApiErrorDTO error = ApiErrorDTO.validation(
                "Invalid request body", 
                "Request body is malformed or missing required fields", 
                getRequestPath(request), 
                correlationId
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        // Legacy fallback
        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .success(false)
                .message("Invalid request body")
                .timestamp(LocalDateTime.now())
                .path(webRequest.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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
