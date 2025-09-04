package com.thunderfat.springboot.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;
import com.thunderfat.springboot.backend.model.service.IPlatoPredetereminadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Plantilla Comida", description = "Operations related to food template management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/plantillacomida")
public class PlantillaComidaRestController {

    private final IPlatoPredetereminadoService platoPredeterminadoService;

    public PlantillaComidaRestController(IPlatoPredetereminadoService platoPredeterminadoService) {
        this.platoPredeterminadoService = platoPredeterminadoService;
    }

    @Operation(summary = "Get food templates by nutritionist", 
               description = "Retrieves all food templates created by a specific nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Food templates retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Nutritionist not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/nutricionista/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<PlatoPredeterminado>>> listarPorNutricionista(
            @Parameter(description = "Nutritionist ID") @PathVariable int id) {
        try {
            List<PlatoPredeterminado> plantillas = platoPredeterminadoService.listarPorNutricionista(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(plantillas, "Food templates retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving food templates: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get food template details", 
               description = "Retrieves detailed information of a specific food template by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Food template retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Food template not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<PlatoPredeterminado>> obtenerPlantilla(
            @Parameter(description = "Food template ID") @PathVariable int id) {
        try {
            PlatoPredeterminado plantilla = platoPredeterminadoService.buscarPorId(id);
            if (plantilla != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(plantilla, "Food template retrieved successfully")
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ManualApiResponseDTO.error("Food template not found with ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving food template: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create food template", 
               description = "Creates a new food template for a nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Food template created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/nutricionista/{id}")
    public ResponseEntity<ManualApiResponseDTO<PlatoPredeterminado>> crearPlantilla(
            @Valid @RequestBody PlatoPredeterminado plantilla, 
            @Parameter(description = "Nutritionist ID") @PathVariable int id) {
        try {
            PlatoPredeterminado nuevaPlantilla = platoPredeterminadoService.insertar(plantilla, id);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(nuevaPlantilla, "Food template created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating food template: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete food template", 
               description = "Deletes a food template by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Food template deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Food template not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminarPlantilla(
            @Parameter(description = "Food template ID") @PathVariable int id) {
        try {
            platoPredeterminadoService.eliminarPlatoPredeterminado(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Food template deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting food template: " + e.getMessage()));
        }
    }
}
