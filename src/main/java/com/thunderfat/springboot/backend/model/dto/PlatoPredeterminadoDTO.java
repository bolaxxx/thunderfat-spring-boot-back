package com.thunderfat.springboot.backend.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO for PlatoPredeterminado entity.
 * Contains all fields from PlatoDTO and adds nutritionist ID and validation.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
    name = "PlatoPredeterminadoDTO",
    description = "Data transfer object for predetermined dishes created by nutritionists"
)
public class PlatoPredeterminadoDTO extends PlatoDTO {
    /**
     * The ID of the nutritionist who created this dish
     */
    @NotNull(message = "Nutricionista ID is required")
    @Schema(
        description = "ID of the nutritionist who created this dish",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer nutricionistaId;
    
    // Validation for fields inherited from PlatoDTO
    // (This ensures validation is enforced at this level too)
    
    @NotBlank(message = "Nombre is required")
    @Override
    public String getNombre() {
        return super.getNombre();
    }
    
    @Positive(message = "KcalTotales must be positive")
    @Override
    public double getKcaltotales() {
        return super.getKcaltotales();
    }
}
