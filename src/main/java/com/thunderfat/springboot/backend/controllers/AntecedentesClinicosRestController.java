package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.AntecedentesClinicosDTO;
import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;
import com.thunderfat.springboot.backend.model.service.IAntecedenteClinicoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modernized Clinical History Controller following Spring Boot 2025 best practices
 * Features:
 * - Manual DTO response wrapper (temporary fix for Lombok issues)
 * - Comprehensive error handling
 * - Modern logging patterns
 * - OpenAPI documentation
 * - Security-ready structure
 */
@Tag(name = "Clinical History", description = "Operations related to clinical history management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/api/v1/antecedentes-clinicos")
@RequiredArgsConstructor
@Slf4j
public class AntecedentesClinicosRestController {
    
    private final IAntecedenteClinicoService antecedenteClinicoService;

    @Operation(summary = "Get clinical history by patient", 
               description = "Retrieves all clinical history records for a specific patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clinical history retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<AntecedentesClinicosDTO>>> listarPorPaciente(
            @Parameter(description = "Patient ID") @PathVariable int id) {
        try {
            log.info("Retrieving clinical history for patient ID: {}", id);
            List<AntecedentesClinicosDTO> antecedentes = antecedenteClinicoService.findByPacienteId(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.<List<AntecedentesClinicosDTO>>builder()
                    .success(true)
                    .data(antecedentes)
                    .message("Clinical history retrieved successfully")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        } catch (Exception e) {
            log.error("Error retrieving clinical history for patient ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.<List<AntecedentesClinicosDTO>>builder()
                    .success(false)
                    .message("Error retrieving clinical history: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    @Operation(summary = "Get clinical history details", 
               description = "Retrieves detailed information of a specific clinical history record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clinical history record retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Clinical history record not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ManualApiResponseDTO<AntecedentesClinicosDTO>> buscarPorId(
            @Parameter(description = "Clinical history ID") @PathVariable int id) {
        try {
            AntecedentesClinicosDTO antecedente = antecedenteClinicoService.findById(id).orElse(null);
            if (antecedente != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.<AntecedentesClinicosDTO>builder()
                        .success(true)
                        .data(antecedente)
                        .message("Clinical history record retrieved successfully")
                        .timestamp(LocalDateTime.now())
                        .build()
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.<AntecedentesClinicosDTO>builder()
                    .success(false)
                    .message("Clinical history record not found")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.<AntecedentesClinicosDTO>builder()
                    .success(false)
                    .message("Error retrieving clinical history record: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    @Operation(summary = "Add clinical history record", 
               description = "Creates a new clinical history record for a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Clinical history record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save/{id}")
    public ResponseEntity<ManualApiResponseDTO<AntecedentesClinicosDTO>> anadirMedicion(
            @Parameter(description = "Patient ID") @PathVariable("id") int id_paciente, 
            @Valid @RequestBody AntecedentesClinicosDTO medicionDTO) {
        try {
            AntecedentesClinicosDTO saved = antecedenteClinicoService.save(medicionDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.<AntecedentesClinicosDTO>builder()
                    .success(true)
                    .data(saved)
                    .message("Clinical history record created successfully")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.<AntecedentesClinicosDTO>builder()
                    .success(false)
                    .message("Error creating clinical history record: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    @Operation(summary = "Update clinical history record", 
               description = "Updates an existing clinical history record (placeholder implementation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clinical history record updated successfully"),
        @ApiResponse(responseCode = "404", description = "Clinical history record not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ManualApiResponseDTO<AntecedentesClinicosDTO>> actualizar(
            @Parameter(description = "Patient ID") @PathVariable("id") int id_paciente, 
            @Valid @RequestBody AntecedentesClinicosDTO medicionDTO) {
        try {
            AntecedentesClinicosDTO updated = antecedenteClinicoService.save(medicionDTO);
            return ResponseEntity.ok(
                ManualApiResponseDTO.<AntecedentesClinicosDTO>builder()
                    .success(true)
                    .data(updated)
                    .message("Clinical history record updated successfully")
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.<AntecedentesClinicosDTO>builder()
                    .success(false)
                    .message("Error updating clinical history record: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    @Operation(summary = "Delete clinical history record", 
               description = "Deletes a clinical history record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Clinical history record deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Clinical history record not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> borrar(
            @Parameter(description = "Clinical history ID") @PathVariable("id") int id_medicion) {
        try {
            antecedenteClinicoService.deleteById(id_medicion);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.<Void>builder()
                    .success(true)
                    .message("Clinical history record deleted successfully")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.<Void>builder()
                    .success(false)
                    .message("Error deleting clinical history record: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }
}
