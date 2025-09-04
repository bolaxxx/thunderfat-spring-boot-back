package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;
import com.thunderfat.springboot.backend.model.service.IComidaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Comidas", description = "Operations related to meal management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/comida")
public class ComidaRestController {
    
    private final IComidaService comidaService;

    public ComidaRestController(IComidaService comidaService) {
        this.comidaService = comidaService;
    }

    @Operation(summary = "Get meal details", 
               description = "Retrieves details of a specific meal by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Meal retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Meal not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Comida>> detalle(
            @Parameter(description = "Meal ID") @PathVariable("id") int idComida) {
        try {
            Comida comida = comidaService.buscarPorID(idComida);
            if (comida != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(comida, "Meal retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Meal not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving meal: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create meal", 
               description = "Creates a new meal for a nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Meal created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ManualApiResponseDTO<Comida>> guardar(
            @Valid @RequestBody Comida comida) {
        try {
            comidaService.insertar(comida);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(comida, "Meal created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating meal: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete meal", 
               description = "Deletes a meal by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Meal deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Meal not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminar(
            @Parameter(description = "Meal ID") @PathVariable("id") int idComida) {
        try {
            comidaService.eliminar(idComida);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Meal deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting meal: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get possible meal substitutions", 
               description = "Retrieves possible meal substitutions for a patient based on a predetermined dish")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Substitutions retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No substitutions found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/cambios/{id_plato}/{id_paciente}")
    public ResponseEntity<ManualApiResponseDTO<List<PlatoPredeterminado>>> buscarPosiblesCambios(
            @Parameter(description = "Predetermined dish ID") @PathVariable("id_plato") int idPlato,
            @Parameter(description = "Patient ID") @PathVariable("id_paciente") int idPaciente) {
        try {
            List<PlatoPredeterminado> cambios = comidaService.bucarcambios(idPaciente, idPlato);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(cambios, "Meal substitutions retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving meal substitutions: " + e.getMessage()));
        }
    }
}
