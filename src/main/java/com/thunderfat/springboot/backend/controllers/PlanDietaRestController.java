package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.dto.PlanDietaDTO;
import com.thunderfat.springboot.backend.model.entity.Ingrediente;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.service.IPlanDietaService;

import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Tag(name = "Plan Dieta", description = "Operations related to diet plan management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/plandieta")
public class PlanDietaRestController {
    
    private final IPlanDietaService planDietaService;

    public PlanDietaRestController(IPlanDietaService planDietaService) {
        this.planDietaService = planDietaService;
    }

    @Operation(summary = "Get diet plans by nutritionist", 
               description = "Retrieves all diet plans for a specific nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Diet plans retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Nutritionist not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<PlanDietaDTO>>> listarPorNutricionista(
            @Parameter(description = "Nutritionist ID") @PathVariable int id) {
        try {
            List<PlanDietaDTO> planes = planDietaService.listarPorNutricionista(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(planes, "Diet plans retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving diet plans: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get diet plan details", 
               description = "Retrieves detailed information of a specific diet plan by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Diet plan retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Diet plan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ManualApiResponseDTO<PlanDietaDTO>> buscarPorId(
            @Parameter(description = "Diet plan ID") @PathVariable int id) {
        try {
            Optional<PlanDietaDTO> dietaOpt = planDietaService.buscarPorId(id);
            if (dietaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ManualApiResponseDTO.error("No diet plan found with ID: " + id));
            }
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(dietaOpt.get(), "Diet plan retrieved successfully")
            );
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Database error: " + e.getMostSpecificCause().getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving diet plan: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create diet plan", 
               description = "Creates a new diet plan for a patient by a nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Diet plan created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save/{nutricionista}/{paciente}")
    public ResponseEntity<ManualApiResponseDTO<Void>> guardarPaciente(
            @Valid @RequestBody PlanDietaDTO newPaciente,
            @Parameter(description = "Nutritionist ID") @PathVariable("nutricionista") int idNutricionista,
            @Parameter(description = "Patient ID") @PathVariable("paciente") int idPaciente) {
        try {
            planDietaService.insertar(newPaciente, idNutricionista, idPaciente);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(null, "Diet plan created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating diet plan: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete diet plan", 
               description = "Deletes a diet plan by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Diet plan deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Diet plan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminarPorId(
            @Parameter(description = "Diet plan ID") @PathVariable int id) {
        try {
            planDietaService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Diet plan deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting diet plan: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update patient", 
               description = "Updates patient information (placeholder implementation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> actualizar(
            @Parameter(description = "Patient ID") @PathVariable("id") int idPaciente, 
            @Valid @RequestBody Paciente paciente) {
        try {
            // Update logic placeholder - implementation needed
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(null, "Patient update functionality not implemented")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error updating patient: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get shopping list", 
               description = "Generates a shopping list of ingredients for a patient's diet plan from a specific start date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shopping list retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient or plan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/listacompra/{id}/{start}")
    public ResponseEntity<ManualApiResponseDTO<List<Ingrediente>>> listaCompra(
            @Parameter(description = "Patient ID") @PathVariable("id") int idPaciente,
            @Parameter(description = "Start date (YYYY-MM-DD)") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("start") LocalDate startDate) {
        try {
            List<Ingrediente> ingredientes = planDietaService.listadelacompra(idPaciente, startDate);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(ingredientes, "Shopping list generated successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error generating shopping list: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get current patient diet plan", 
               description = "Retrieves the current active diet plan for a patient from a specific start date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current diet plan retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No active plan found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/paciente/{id}/{start}")
    public ResponseEntity<ManualApiResponseDTO<PlanDietaDTO>> planPaciente(
            @Parameter(description = "Patient ID") @PathVariable("id") int idPaciente,
            @Parameter(description = "Start date (YYYY-MM-DD)") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("start") LocalDate startDate) {
        try {
            Optional<PlanDietaDTO> planOpt = planDietaService.buscarPlanActualPaciente(idPaciente, startDate);
            if (planOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ManualApiResponseDTO.error("No active diet plan found for patient"));
            }
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(planOpt.get(), "Current diet plan retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving current diet plan: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update diet plan", 
               description = "Updates an existing diet plan for a patient by a nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Diet plan updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/update/{id_nutricionista}/{id_paciente}")
    public ResponseEntity<ManualApiResponseDTO<Void>> update(
            @Parameter(description = "Nutritionist ID") @PathVariable int id_nutricionista, 
            @Parameter(description = "Patient ID") @PathVariable int id_paciente, 
            @Valid @RequestBody PlanDietaDTO dieta) {
        try {
            planDietaService.updatePlan(dieta, id_nutricionista, id_paciente);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(null, "Diet plan updated successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error updating diet plan: " + e.getMessage()));
        }
    }
}
