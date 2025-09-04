package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dto.AntecedenteTratamientoDTO;
import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;
import com.thunderfat.springboot.backend.model.service.IAntecedenteTratamientoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Treatment History", description = "Operations related to treatment history management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/api/v1/antecedente-tratamiento")
@RequiredArgsConstructor
@Slf4j
public class AntecedentesTratamientoRestController {
    
    private final IAntecedenteTratamientoService antecedenteTratamientoService;

    // ============= Legacy Endpoints (Deprecated) =============
    
    /**
     * @deprecated Use {@link #getByPatientId(int)} instead.
     */
    @Deprecated
    @Operation(summary = "[DEPRECATED] Get treatment history by patient", 
               description = "Retrieves all treatment history records for a specific patient")
    @GetMapping("/legacy/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<AntecedenteTratamiento>>> listarPorPaciente(
            @Parameter(description = "Patient ID") @PathVariable int id) {
        try {
            List<AntecedenteTratamiento> antecedentes = antecedenteTratamientoService.buscarPorPaciente(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(
                    antecedentes,
                    "Treatment history retrieved successfully"
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error retrieving treatment history: " + e.getMessage()
                ));
        }
    }

    /**
     * @deprecated Use {@link #getById(int)} instead.
     */
    @Deprecated
    @Operation(summary = "[DEPRECATED] Get treatment history details", 
               description = "Retrieves detailed information of a specific treatment history record by its ID")
    @GetMapping("/legacy/detalle/{id}")
    public ResponseEntity<ManualApiResponseDTO<AntecedenteTratamiento>> buscarPorId(
            @Parameter(description = "Treatment history ID") @PathVariable int id) {
        try {
            AntecedenteTratamiento antecedente = antecedenteTratamientoService.buscarPorId(id);
            if (antecedente != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(
                        antecedente,
                        "Treatment history record retrieved successfully"
                    )
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(
                    "Treatment history record not found"
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error retrieving treatment history record: " + e.getMessage()
                ));
        }
    }

    /**
     * @deprecated Use {@link #create(int, AntecedenteTratamientoDTO)} instead.
     */
    @Deprecated
    @Operation(summary = "[DEPRECATED] Add treatment history record", 
               description = "Creates a new treatment history record for a patient")
    @PostMapping("/legacy/save/{id}")
    public ResponseEntity<ManualApiResponseDTO<AntecedenteTratamiento>> anadirMedicion(
            @Parameter(description = "Patient ID") @PathVariable("id") int id_paciente, 
            @Valid @RequestBody AntecedenteTratamiento medicion) {
        try {
            antecedenteTratamientoService.insertar(medicion, id_paciente);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(
                    medicion,
                    "Treatment history record created successfully"
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error(
                    "Error creating treatment history record: " + e.getMessage()
                ));
        }
    }

    /**
     * @deprecated Use {@link #update(int, AntecedenteTratamientoDTO)} instead.
     */
    @Deprecated
    @Operation(summary = "[DEPRECATED] Update treatment history record", 
               description = "Updates an existing treatment history record (placeholder implementation)")
    @PutMapping("/legacy/actualizar/{id}")
    public ResponseEntity<ManualApiResponseDTO<AntecedenteTratamiento>> actualizar(
            @Parameter(description = "Patient ID") @PathVariable("id") int id_paciente, 
            @Valid @RequestBody AntecedenteTratamiento medicion) {
        try {
            // Update logic placeholder - implementation needed
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(
                    medicion,
                    "Treatment history record update functionality not implemented"
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error updating treatment history record: " + e.getMessage()
                ));
        }
    }

    /**
     * @deprecated Use {@link #delete(int)} instead.
     */
    @Deprecated
    @Operation(summary = "[DEPRECATED] Delete treatment history record", 
               description = "Deletes a treatment history record by its ID")
    @DeleteMapping("/legacy/eliminar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> borrar(
            @Parameter(description = "Treatment history ID") @PathVariable("id") int id_medicion) {
        try {
            antecedenteTratamientoService.eliminar(id_medicion);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(
                    null,
                    "Treatment history record deleted successfully"
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error deleting treatment history record: " + e.getMessage()
                ));
        }
    }
    
    // ============= Modern Endpoints =============
    
    @Operation(summary = "Get all treatment history records", 
               description = "Retrieves all treatment history records")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Treatment history records retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<ManualApiResponseDTO<List<AntecedenteTratamientoDTO>>> getAll() {
        log.debug("REST request to get all treatment history records");
        try {
            List<AntecedenteTratamientoDTO> antecedentes = antecedenteTratamientoService.findAll();
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(
                    antecedentes,
                    "Treatment history records retrieved successfully"
                )
            );
        } catch (Exception e) {
            log.error("Error retrieving all treatment history records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error retrieving treatment history records: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Get treatment history by ID", 
               description = "Retrieves a specific treatment history record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Treatment history record retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Treatment history record not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<AntecedenteTratamientoDTO>> getById(
            @Parameter(description = "Treatment history ID") @PathVariable int id) {
        log.debug("REST request to get treatment history record with ID: {}", id);
        try {
            AntecedenteTratamientoDTO antecedente = antecedenteTratamientoService.findById(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(
                    antecedente,
                    "Treatment history record retrieved successfully"
                )
            );
        } catch (ResourceNotFoundException e) {
            log.error("Treatment history record not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(
                    e.getMessage()
                ));
        } catch (Exception e) {
            log.error("Error retrieving treatment history record with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error retrieving treatment history record: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Get treatment history records by patient", 
               description = "Retrieves all treatment history records for a specific patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Treatment history records retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<ManualApiResponseDTO<List<AntecedenteTratamientoDTO>>> getByPatientId(
            @Parameter(description = "Patient ID") @PathVariable int idPaciente) {
        log.debug("REST request to get treatment history records for patient with ID: {}", idPaciente);
        try {
            List<AntecedenteTratamientoDTO> antecedentes = antecedenteTratamientoService.findByPatientId(idPaciente);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(
                    antecedentes,
                    "Treatment history records retrieved successfully"
                )
            );
        } catch (ResourceNotFoundException e) {
            log.error("Patient not found with ID: {}", idPaciente, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(
                    e.getMessage()
                ));
        } catch (Exception e) {
            log.error("Error retrieving treatment history records for patient with ID: {}", idPaciente, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error retrieving treatment history records: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Create treatment history record", 
               description = "Creates a new treatment history record for a patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Treatment history record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/patient/{idPaciente}")
    public ResponseEntity<ManualApiResponseDTO<AntecedenteTratamientoDTO>> create(
            @Parameter(description = "Patient ID") @PathVariable int idPaciente, 
            @Valid @RequestBody AntecedenteTratamientoDTO antecedenteTratamientoDTO) {
        log.debug("REST request to create treatment history record for patient with ID: {}", idPaciente);
        try {
            AntecedenteTratamientoDTO created = antecedenteTratamientoService.save(antecedenteTratamientoDTO, idPaciente);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(
                    created,
                    "Treatment history record created successfully"
                ));
        } catch (ResourceNotFoundException e) {
            log.error("Patient not found with ID: {}", idPaciente, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(
                    e.getMessage()
                ));
        } catch (Exception e) {
            log.error("Error creating treatment history record for patient with ID: {}", idPaciente, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error creating treatment history record: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Update treatment history record", 
               description = "Updates an existing treatment history record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Treatment history record updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Treatment history record not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<AntecedenteTratamientoDTO>> update(
            @Parameter(description = "Treatment history ID") @PathVariable int id, 
            @Valid @RequestBody AntecedenteTratamientoDTO antecedenteTratamientoDTO) {
        log.debug("REST request to update treatment history record with ID: {}", id);
        try {
            AntecedenteTratamientoDTO updated = antecedenteTratamientoService.update(id, antecedenteTratamientoDTO);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(
                    updated,
                    "Treatment history record updated successfully"
                )
            );
        } catch (ResourceNotFoundException e) {
            log.error("Treatment history record not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(
                    e.getMessage()
                ));
        } catch (Exception e) {
            log.error("Error updating treatment history record with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error updating treatment history record: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Delete treatment history record", 
               description = "Deletes a treatment history record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Treatment history record deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Treatment history record not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> delete(
            @Parameter(description = "Treatment history ID") @PathVariable int id) {
        log.debug("REST request to delete treatment history record with ID: {}", id);
        try {
            antecedenteTratamientoService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(
                    null,
                    "Treatment history record deleted successfully"
                ));
        } catch (ResourceNotFoundException e) {
            log.error("Treatment history record not found with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(
                    e.getMessage()
                ));
        } catch (Exception e) {
            log.error("Error deleting treatment history record with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error deleting treatment history record: " + e.getMessage()
                ));
        }
    }
}
