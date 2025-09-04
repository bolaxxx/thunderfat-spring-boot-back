package com.thunderfat.springboot.backend.model.dao.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.thunderfat.springboot.backend.model.entity.Paciente;

import jakarta.persistence.criteria.Predicate;

/**
 * JPA Criteria API Specifications for Paciente entity.
 * This class provides type-safe, reusable query predicates following Spring Boot 2025 best practices.
 * 
 * Benefits of using Specifications:
 * - Type safety at compile time
 * - Reusable query components
 * - Better performance than string-based queries
 * - Easy to combine and test
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
public class PacienteSpecifications {
    
    /**
     * Specification to filter patients by nutritionist ID.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return specification for nutritionist filter
     */
    public static Specification<Paciente> belongsToNutritionist(Integer nutricionistaId) {
        return (root, query, criteriaBuilder) -> {
            if (nutricionistaId == null) {
                return criteriaBuilder.conjunction(); // Always true
            }
            return criteriaBuilder.equal(root.get("nutricionista").get("id"), nutricionistaId);
        };
    }
    
    /**
     * Specification for case-insensitive name search (first name or last name).
     * 
     * @param searchTerm the search term
     * @return specification for name search
     */
    public static Specification<Paciente> nameContainsIgnoreCase(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            
            Predicate firstNameMatch = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("nombre")), pattern
            );
            Predicate lastNameMatch = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("apellidos")), pattern
            );
            Predicate fullNameMatch = criteriaBuilder.like(
                criteriaBuilder.lower(
                    criteriaBuilder.concat(
                        criteriaBuilder.concat(root.get("nombre"), " "),
                        root.get("apellidos")
                    )
                ), pattern
            );
            
            return criteriaBuilder.or(firstNameMatch, lastNameMatch, fullNameMatch);
        };
    }
    
    /**
     * Specification for DNI search (partial matching).
     * 
     * @param dni the DNI search term
     * @return specification for DNI search
     */
    public static Specification<Paciente> dniContainsIgnoreCase(String dni) {
        return (root, query, criteriaBuilder) -> {
            if (dni == null || dni.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("dni")), 
                "%" + dni.toLowerCase() + "%"
            );
        };
    }
    
    /**
     * Specification for phone number search.
     * 
     * @param telefono the phone number search term
     * @return specification for phone search
     */
    public static Specification<Paciente> phoneContains(String telefono) {
        return (root, query, criteriaBuilder) -> {
            if (telefono == null || telefono.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("telefono"), "%" + telefono + "%");
        };
    }
    
    /**
     * Specification for email search (case-insensitive).
     * 
     * @param email the email search term
     * @return specification for email search
     */
    public static Specification<Paciente> emailContainsIgnoreCase(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("email")), 
                "%" + email.toLowerCase() + "%"
            );
        };
    }
    
    /**
     * Specification for birth date range filter.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return specification for birth date range
     */
    public static Specification<Paciente> birthDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            
            if (startDate == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("fechanacimiento"), endDate);
            }
            
            if (endDate == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("fechanacimiento"), startDate);
            }
            
            return criteriaBuilder.between(root.get("fechanacimiento"), startDate, endDate);
        };
    }
    
    /**
     * Specification for locality filter (case-insensitive).
     * 
     * @param localidad the locality name
     * @return specification for locality filter
     */
    public static Specification<Paciente> fromLocalidad(String localidad) {
        return (root, query, criteriaBuilder) -> {
            if (localidad == null || localidad.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                criteriaBuilder.lower(root.get("localidad")), 
                localidad.toLowerCase()
            );
        };
    }
    
    /**
     * Specification for active/enabled patients only.
     * 
     * @return specification for active patients
     */
    public static Specification<Paciente> isActive() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isTrue(root.get("enabled"));
    }
    
    /**
     * Combined specification for comprehensive patient search.
     * This demonstrates how to combine multiple specifications.
     * 
     * @param nutricionistaId the nutritionist ID (required)
     * @param searchTerm general search term (optional)
     * @param localidad locality filter (optional)
     * @param activeOnly whether to include only active patients
     * @return combined specification
     */
    public static Specification<Paciente> comprehensiveSearch(
            Integer nutricionistaId, 
            String searchTerm, 
            String localidad, 
            boolean activeOnly) {
        
        Specification<Paciente> spec = belongsToNutritionist(nutricionistaId);
        
        if (activeOnly) {
            spec = spec.and(isActive());
        }
        
        if (localidad != null && !localidad.trim().isEmpty()) {
            spec = spec.and(fromLocalidad(localidad));
        }
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            Specification<Paciente> searchSpec = nameContainsIgnoreCase(searchTerm)
                    .or(dniContainsIgnoreCase(searchTerm))
                    .or(phoneContains(searchTerm))
                    .or(emailContainsIgnoreCase(searchTerm));
            spec = spec.and(searchSpec);
        }
        
        return spec;
    }
}
