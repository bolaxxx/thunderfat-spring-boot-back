package com.thunderfat.springboot.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta}
 * Represents a dish within a diet plan with specific quantity.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlatoPlanDietaDTO extends PlatoDTO {
    
    /**
     * The amount of this dish in the diet plan
     * Must be positive
     */
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Double cantidad;
    
    // Relationship IDs - for linking to related entities
    private Integer comidaId;
    private Integer planDietaId;
    
    // Overridden getters/setters to provide more specific validation
    
    @Override
    @NotBlank(message = "Name is required")
    public String getNombre() {
        return super.getNombre();
    }
    
    @Override
    @NotNull(message = "Total protein value is required")
    @PositiveOrZero(message = "Total protein must be zero or positive")
    public double getProteinastotales() {
        return super.getProteinastotales();
    }
    
    @Override
    @NotNull(message = "Total fat value is required")
    @PositiveOrZero(message = "Total fat must be zero or positive")
    public double getGrasastotales() {
        return super.getGrasastotales();
    }
    
    @Override
    @NotNull(message = "Total calories value is required")
    @PositiveOrZero(message = "Total calories must be zero or positive")
    public double getKcaltotales() {
        return super.getKcaltotales();
    }
    
    @Override
    @NotNull(message = "Total carbohydrates value is required")
    @PositiveOrZero(message = "Total carbohydrates must be zero or positive")
    public double getHidratostotales() {
        return super.getHidratostotales();
    }
    
    @Override
    @NotNull(message = "Total fiber value is required")
    @PositiveOrZero(message = "Total fiber must be zero or positive")
    public double getFibra() {
        return super.getFibra();
    }
}
