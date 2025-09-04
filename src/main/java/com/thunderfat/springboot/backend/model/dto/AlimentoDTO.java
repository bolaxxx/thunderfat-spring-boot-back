package com.thunderfat.springboot.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO for Alimento (Food) entity with comprehensive validation
 * Follows Spring Boot 2025 best practices for data transfer objects
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlimentoDTO {
    
    private Integer id;
    
    @NotBlank(message = "El nombre del alimento es obligatorio", 
              groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El estado del alimento es obligatorio",
              groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(max = 50, message = "El estado no puede exceder 50 caracteres")
    private String estado;
    
    // Nutritional values with validation (matching entity fields)
    @NotNull(message = "Las calorías son obligatorias", 
             groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @PositiveOrZero(message = "Las calorías no pueden ser negativas")
    @DecimalMin(value = "0.0", message = "Las calorías deben ser un valor positivo")
    private Double cal;
    
    @PositiveOrZero(message = "Los hidratos de carbono no pueden ser negativos")
    private Double hidratosdecarbono;
    
    @PositiveOrZero(message = "El contenido de agua no puede ser negativo")
    private Double h2o;
    
    @PositiveOrZero(message = "El valor no específico no puede ser negativo")
    private Double noespecifico;
    
    @PositiveOrZero(message = "Las grasas no pueden ser negativas")
    private Double grasas;
    
    @PositiveOrZero(message = "Las proteínas no pueden ser negativas")
    private Double proteinas;
    
    // Vitamins validation
    @PositiveOrZero(message = "La vitamina A no puede ser negativa")
    private Double vitamina;
    
    @PositiveOrZero(message = "La vitamina B2 no puede ser negativa")
    private Double vitaminb2;
    
    @PositiveOrZero(message = "La vitamina B1 no puede ser negativa")
    private Double vitaminb1;
    
    @PositiveOrZero(message = "La vitamina C no puede ser negativa")
    private Double vitaminc;
    
    @PositiveOrZero(message = "La niacina no puede ser negativa")
    private Double niac;
    
    // Minerals validation
    @PositiveOrZero(message = "El cobre no puede ser negativo")
    private Double cobre;
    
    @PositiveOrZero(message = "El potasio no puede ser negativo")
    private Double potasio;
    
    @PositiveOrZero(message = "El sodio no puede ser negativo")
    private Double sodio;
    
    @PositiveOrZero(message = "El azufre no puede ser negativo")
    private Double azufre;
    
    @PositiveOrZero(message = "El calcio no puede ser negativo")
    private Double calcio;
    
    @PositiveOrZero(message = "El fósforo no puede ser negativo")
    private Double fosforo;
    
    @PositiveOrZero(message = "El hierro no puede ser negativo")
    private Double hierro;
    
    @PositiveOrZero(message = "El magnesio no puede ser negativo")
    private Double magnesio;
    
    @PositiveOrZero(message = "El cloro no puede ser negativo")
    private Double cloro;
    
    // Amino acids validation
    @PositiveOrZero(message = "La metionina no puede ser negativa")
    private Double met;
    
    @PositiveOrZero(message = "La lisina no puede ser negativa")
    private Double lis;
    
    @PositiveOrZero(message = "La leucina no puede ser negativa")
    private Double leu;
    
    @PositiveOrZero(message = "La isoleucina no puede ser negativa")
    private Double illeu;
    
    @PositiveOrZero(message = "La treonina no puede ser negativa")
    private Double tre;
    
    @PositiveOrZero(message = "El triptófano no puede ser negativo")
    private Double tri;
    
    @PositiveOrZero(message = "La fenilalanina no puede ser negativa")
    private Double fen;
    
    @PositiveOrZero(message = "La valina no puede ser negativa")
    private Double val;
    
    @PositiveOrZero(message = "Los ácidos no pueden ser negativos")
    private Double acid;
    
    @PositiveOrZero(message = "Los alcalinos no pueden ser negativos")
    private Double alcal;
    
    /**
     * Validates nutritional completeness for business rules
     */
    public boolean isNutritionallyComplete() {
        return cal != null && cal > 0 && 
               (hidratosdecarbono != null || grasas != null || proteinas != null);
    }
    
    /**
     * Calculates total macronutrients
     */
    public double getTotalMacronutrients() {
        double carbs = hidratosdecarbono != null ? hidratosdecarbono : 0.0;
        double fats = grasas != null ? grasas : 0.0;
        double proteins = proteinas != null ? proteinas : 0.0;
        return carbs + fats + proteins;
    }
}
