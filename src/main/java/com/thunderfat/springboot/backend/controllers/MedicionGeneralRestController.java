package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.service.IMedicionGeneralService;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
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

@Tag(name = "General Measurements", description = "Operations related to general body measurements management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/medicion_general")
public class MedicionGeneralRestController {
    
    private final IMedicionGeneralService medicionService;

    public MedicionGeneralRestController(IMedicionGeneralService medicionService) {
        this.medicionService = medicionService;
    }

    @Operation(summary = "Get measurements by patient", 
               description = "Retrieves all general measurements for a specific patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurements retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<MedicionGeneral>>> listarPorPaciente(
            @Parameter(description = "Patient ID") @PathVariable int id) {
        try {
            List<MedicionGeneral> mediciones = medicionService.listarPorPaciente(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(mediciones, "General measurements retrieved successfully")
            );    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving general measurements: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get measurement details", 
               description = "Retrieves detailed information of a specific general measurement by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Measurement not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ManualApiResponseDTO<MedicionGeneral>> buscarPorId(
            @Parameter(description = "Measurement ID") @PathVariable int id) {
        try {
            Optional<MedicionGeneral> medicionOpt = medicionService.buscarPorId(id);
            if (medicionOpt.isPresent()) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(medicionOpt.get(), "General measurement retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("General measurement not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving general measurement: " + e.getMessage()));
        }
    }

    @Operation(summary = "Add general measurement", 
               description = "Creates a new general measurement for a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Measurement created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save/{id}")
    public ResponseEntity<ManualApiResponseDTO<MedicionGeneral>> anadirMedicion(
            @Parameter(description = "Patient ID") @PathVariable("id") int id_paciente, 
            @Valid @RequestBody MedicionGeneral medicion) {
        try {
            MedicionGeneral medicionResponse = medicionService.insertar(medicion, id_paciente);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(medicionResponse, "General measurement created successfully"));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Database error: " + e.getMostSpecificCause().getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating general measurement: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update general measurement", 
               description = "Updates an existing general measurement")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement updated successfully"),
        @ApiResponse(responseCode = "404", description = "Measurement not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ManualApiResponseDTO<MedicionGeneral>> actualizar(
            @Parameter(description = "Patient ID") @PathVariable("id") int id_paciente, 
            @Valid @RequestBody MedicionGeneral medicion) {
        try {
            medicionService.actualizar(medicion);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(medicion, "General measurement updated successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error updating general measurement: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete general measurement", 
               description = "Deletes a general measurement by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Measurement deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Measurement not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> borrar(
            @Parameter(description = "Measurement ID") @PathVariable("id") int id_medicion) {
        try {
            medicionService.eliminar(id_medicion);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "General measurement deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting general measurement: " + e.getMessage()));
        }
    }
}
