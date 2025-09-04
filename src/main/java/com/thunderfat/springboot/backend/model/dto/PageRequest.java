package com.thunderfat.springboot.backend.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard pagination request parameters for all paginated endpoints
 * Provides consistent pagination interface across the API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    
    @Min(value = 0, message = "Page number must be 0 or greater")
    @Builder.Default
    private int page = 0;
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    @Builder.Default
    private int size = 20;
    
    @Builder.Default
    private String sortBy = "id";
    
    @Builder.Default
    private String sortDirection = "ASC";
    
    private String filter;
    
    /**
     * Convert to Spring Data Pageable
     */
    public org.springframework.data.domain.Pageable toPageable() {
        org.springframework.data.domain.Sort.Direction direction = 
            "DESC".equalsIgnoreCase(sortDirection) ? 
                org.springframework.data.domain.Sort.Direction.DESC : 
                org.springframework.data.domain.Sort.Direction.ASC;
                
        return org.springframework.data.domain.PageRequest.of(
            page, 
            size, 
            org.springframework.data.domain.Sort.by(direction, sortBy)
        );
    }
}
