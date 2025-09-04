package com.thunderfat.springboot.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;
import com.thunderfat.springboot.backend.model.service.IMedicionSegmentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Mediciones Segmentales", description = "Operations related to segmental body measurements")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/medicion_segmental")
public class MedicionSegmentalRestController {
    
    private final IMedicionSegmentalService medicionService;

    public MedicionSegmentalRestController(IMedicionSegmentalService medicionService) {
        this.medicionService = medicionService;
    }

    @Operation(summary = "Get segmental measurements by patient", 
               description = "Retrieves all segmental measurements for a specific patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurements retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<MedicionSegmental>>> listarPorPaciente(
            @Parameter(description = "Patient ID") @PathVariable int id) {
        try {
            List<MedicionSegmental> mediciones = medicionService.listarPorPaciente(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(mediciones, "Segmental measurements retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving segmental measurements: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get segmental measurement by ID", 
               description = "Retrieves a specific segmental measurement by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Measurement not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ManualApiResponseDTO<MedicionSegmental>> buscarPorId(
            @Parameter(description = "Measurement ID") @PathVariable int id) {
        try {
            MedicionSegmental medicion = medicionService.buscarPorID(id);
            if (medicion != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(medicion, "Segmental measurement retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Segmental measurement not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving segmental measurement: " + e.getMessage()));
        }
    }

    @Operation(summary = "Add segmental measurement", 
               description = "Creates a new segmental measurement for a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Measurement created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> anadirMedicion(
            @Parameter(description = "Patient ID") @PathVariable("id") int id_paciente, 
            @Valid @RequestBody MedicionSegmental medicion) {
        try {
            medicionService.insertar(medicion, id_paciente);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(null, "Segmental measurement created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating segmental measurement: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update segmental measurement", 
               description = "Updates an existing segmental measurement for a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Measurement not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> actualizar(
            @Parameter(description = "Patient ID") @PathVariable("id") int id_paciente, 
            @Valid @RequestBody MedicionSegmental medicion) {
        try {
            medicionService.actualizar(medicion, id_paciente);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(null, "Segmental measurement updated successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error updating segmental measurement: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete segmental measurement", 
               description = "Deletes a segmental measurement by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Measurement deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Measurement not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminar(
            @Parameter(description = "Measurement ID") @PathVariable("id") int id_medicion) {
        try {
            medicionService.eliminar(id_medicion);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Segmental measurement deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting segmental measurement: " + e.getMessage()));
        }
    }
}
