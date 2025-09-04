package com.thunderfat.springboot.backend.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO for Plato (dish) entity.
 * Contains basic dish information including name, ingredients, recipe and nutritional values.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Data
@Schema(
    name = "PlatoDTO",
    description = "Data transfer object for dishes containing recipe and nutritional information"
)
public class PlatoDTO {
    @Schema(description = "Unique identifier for the dish", example = "1")
    private int id;
    
    @Schema(description = "Name of the dish", example = "Ensalada Mediterránea")
    private String nombre;
    
    @Schema(description = "List of ingredients used in this dish")
    private List<IngredienteDTO> ingredientes;
    
    @Schema(description = "Cooking instructions for preparing the dish", example = "1. Mix vegetables\n2. Add olive oil\n3. Season with salt and pepper")
    private String receta;
    
    @Schema(description = "Total protein content in grams", example = "15.5")
    private double proteinastotales;
    
    @Schema(description = "Total fat content in grams", example = "12.3")
    private double grasastotales;
    
    @Schema(description = "Total caloric content in kcal", example = "320.5")
    private double kcaltotales;
    
    @Schema(description = "Total carbohydrate content in grams", example = "45.2")
    private double hidratostotales;
    
    @Schema(description = "Dietary fiber content in grams", example = "8.3")
    private double fibra;
    
    @Schema(description = "Salt content in grams", example = "1.2")
    private double sal;
    
    @Schema(description = "Sugar content in grams", example = "5.6")
    private double azucar;
}
