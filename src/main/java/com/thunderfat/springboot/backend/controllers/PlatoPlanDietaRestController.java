package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;
import com.thunderfat.springboot.backend.model.service.IPlatoPlanDietaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Diet Plan Dishes", description = "Operations related to diet plan dish management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/platoplandieta")
public class PlatoPlanDietaRestController {
    
    private final IPlatoPlanDietaService platoPlanDietaService;

    public PlatoPlanDietaRestController(IPlatoPlanDietaService platoPlanDietaService) {
        this.platoPlanDietaService = platoPlanDietaService;
    }

    @Operation(summary = "Get diet plan dish by ID", 
               description = "Retrieves a specific diet plan dish by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Diet plan dish retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Diet plan dish not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<PlatoPlanDieta>> buscarPorId(
            @Parameter(description = "Diet plan dish ID") @PathVariable int id) {
        try {
            PlatoPlanDieta platoPlanDieta = platoPlanDietaService.buscarPorId(id);
            if (platoPlanDieta != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(platoPlanDieta, "Diet plan dish retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Diet plan dish not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving diet plan dish: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create diet plan dish", 
               description = "Creates a new diet plan dish")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Diet plan dish created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save")
    public ResponseEntity<ManualApiResponseDTO<PlatoPlanDieta>> guardar(
            @Valid @RequestBody PlatoPlanDieta platoPlanDieta) {
        try {
            platoPlanDietaService.insertar(platoPlanDieta);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(platoPlanDieta, "Diet plan dish created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating diet plan dish: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete diet plan dish", 
               description = "Deletes a diet plan dish by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Diet plan dish deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Diet plan dish not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminar(
            @Parameter(description = "Diet plan dish ID") @PathVariable int id) {
        try {
            platoPlanDietaService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Diet plan dish deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting diet plan dish: " + e.getMessage()));
        }
    }
}
