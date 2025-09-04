package com.thunderfat.springboot.backend.validation;

/**
 * Validation groups for controlling when validation constraints are applied.
 * Follows Spring Boot 2025 best practices for validation grouping.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
public interface ValidationGroups {
    
    /**
     * Validation group for entity creation operations.
     * Applied when creating new entities to ensure all required fields are present.
     */
    interface Create {}
    
    /**
     * Validation group for entity update operations.
     * Applied when updating existing entities, may have different requirements than creation.
     */
    interface Update {}
    
    /**
     * Validation group for authentication operations.
     * Applied during login, registration, and password operations.
     */
    interface Authentication {}
    
    /**
     * Validation group for search operations.
     * Applied when performing search queries with specific parameter requirements.
     */
    interface Search {}
    
    /**
     * Validation group for business rule validation.
     * Applied for complex business logic validation that goes beyond basic field validation.
     */
    interface BusinessRules {}
}
