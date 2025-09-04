package com.thunderfat.springboot.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.dto.IngredienteDTO;
import com.thunderfat.springboot.backend.model.service.IIngredienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Ingredientes", description = "Operations related to ingredient management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/ingredientes")
public class IngredienteRestController {

    private final IIngredienteService ingredienteService;

    public IngredienteRestController(IIngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @Operation(summary = "Get all ingredients", description = "Retrieves a list of all ingredients in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingredients retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_NUTRICIONISTA')")
    @GetMapping
    public ResponseEntity<ManualApiResponseDTO<List<IngredienteDTO>>> listarIngredientes() {
        try {
            List<IngredienteDTO> ingredientes = ingredienteService.listar();
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(ingredientes, "Ingredients retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving ingredients: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get ingredient by ID", description = "Retrieves a specific ingredient by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ingredient retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Ingredient not found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_NUTRICIONISTA')")
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<IngredienteDTO>> obtenerIngrediente(
            @Parameter(description = "Ingredient ID") @PathVariable int id) {
        try {
            IngredienteDTO ingrediente = ingredienteService.buscarPorId(id);
            if (ingrediente != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(ingrediente, "Ingredient retrieved successfully")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ManualApiResponseDTO.error("Ingredient not found with ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving ingredient: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create ingredient", description = "Creates a new ingredient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ingredient created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_NUTRICIONISTA')")
    @PostMapping
    public ResponseEntity<ManualApiResponseDTO<IngredienteDTO>> crearIngrediente(
            @Valid @RequestBody IngredienteDTO ingredienteDTO) {
        try {
            ingredienteService.insertar(ingredienteDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(ingredienteDTO, "Ingredient created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating ingredient: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete ingredient", description = "Deletes an ingredient by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ingredient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Ingredient not found"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_NUTRICIONISTA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminarIngrediente(
            @Parameter(description = "Ingredient ID") @PathVariable int id) {
        try {
            ingredienteService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Ingredient deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting ingredient: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Calculate nutritional values", 
               description = "Calculates nutritional values for an ingredient based on food and quantity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nutritional values calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_NUTRICIONISTA')")
    @PostMapping("/calcular-valores-nutricionales")
    public ResponseEntity<ManualApiResponseDTO<IngredienteDTO>> calcularValoresNutricionales(
            @Valid @RequestBody IngredienteDTO ingredienteDTO) {
        try {
            IngredienteDTO resultado = ingredienteService.calcularValoresNutricionales(ingredienteDTO);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(resultado, "Nutritional values calculated successfully")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error calculating nutritional values: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error calculating nutritional values: " + e.getMessage()));
        }
    }
}
