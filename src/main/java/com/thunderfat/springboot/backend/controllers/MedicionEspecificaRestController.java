package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;
import com.thunderfat.springboot.backend.model.service.IMedicionEspecificaService;

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

@Tag(name = "Mediciones Específicas", description = "Operations related to specific body measurements")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/medicion_especifica")
public class MedicionEspecificaRestController {
    private final IMedicionEspecificaService medicionService;

    public MedicionEspecificaRestController(IMedicionEspecificaService medicionService) {
        this.medicionService = medicionService;
    }

    @Operation(summary = "Get specific measurements by patient", 
               description = "Retrieves all specific measurements for a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurements retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<MedicionEspecifica>>> listarPorPaciente(
            @Parameter(description = "Patient ID") @PathVariable int id) {
        try {
            List<MedicionEspecifica> mediciones = medicionService.listarPorPaciente(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(mediciones, "Specific measurements retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving specific measurements: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get specific measurement by ID", 
               description = "Retrieves a specific measurement by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Measurement retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Measurement not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ManualApiResponseDTO<MedicionEspecifica>> buscarPorId(
            @Parameter(description = "Measurement ID") @PathVariable int id) {
        try {
            MedicionEspecifica medicion = medicionService.buscarPorId(id);
            if (medicion != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(medicion, "Specific measurement retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Specific measurement not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving specific measurement: " + e.getMessage()));
        }
    }

    @Operation(summary = "Add specific measurement", 
               description = "Creates a new specific measurement for a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Measurement created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<MedicionEspecifica>> anadirMedicion(
            @Parameter(description = "Patient ID") @PathVariable("id") int idPaciente, 
            @Valid @RequestBody MedicionEspecifica medicion) {
        try {
            medicionService.insertar(medicion, idPaciente);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(medicion, "Specific measurement created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating specific measurement: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete specific measurement", 
               description = "Deletes a specific measurement by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Measurement deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Measurement not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> borrar(
            @Parameter(description = "Measurement ID") @PathVariable("id") int idMedicion) {
        try {
            medicionService.eliminar(idMedicion);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Specific measurement deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting specific measurement: " + e.getMessage()));
        }
    }
}
