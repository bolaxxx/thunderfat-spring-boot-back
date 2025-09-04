package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dto.DiaDietaDTO;
import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.DiaDieta;
import com.thunderfat.springboot.backend.model.service.IDIaDietaService;

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

/**
 * Controlador REST para la gestión de días de dieta.
 */
@Tag(name = "Daily Diet", description = "Operations related to daily diet management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/diadieta")
@RequiredArgsConstructor
@Slf4j
public class DiaDietaRestController {
    
    private final IDIaDietaService diaDietaService;

    // ============= Métodos Legacy (Deprecados) =============
    
    /**
     * @deprecated Use {@link #getDiaDietaById(int)} instead.
     */
    @Operation(summary = "Get daily diet by ID (Legacy)", 
               description = "Retrieves a specific daily diet by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Daily diet retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Daily diet not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    @Deprecated
    public ResponseEntity<ManualApiResponseDTO<DiaDieta>> buscarPorId(
            @Parameter(description = "Daily diet ID") @PathVariable int id) {
        log.debug("Método deprecado: buscarPorId({})", id);
        try {
            DiaDieta diaDieta = diaDietaService.buscarporID(id);
            if (diaDieta != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(diaDieta, "Daily diet retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Daily diet not found"));
        } catch (Exception e) {
            log.error("Error al buscar día de dieta por ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving daily diet: " + e.getMessage()));
        }
    }

    /**
     * @deprecated Use {@link #getDiaDietasByPlanId(int)} instead.
     */
    @Operation(summary = "Get daily diets by diet plan (Legacy)", 
               description = "Retrieves all daily diets for a specific diet plan")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Daily diets retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Diet plan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/plan/{id_plan}")
    @Deprecated
    public ResponseEntity<ManualApiResponseDTO<List<DiaDieta>>> buscarPorPlan(
            @Parameter(description = "Diet plan ID") @PathVariable int id_plan) {
        log.debug("Método deprecado: buscarPorPlan({})", id_plan);
        try {
            List<DiaDieta> diasDieta = diaDietaService.buscarporPlan(id_plan);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(diasDieta, "Daily diets retrieved successfully")
            );
        } catch (Exception e) {
            log.error("Error al buscar días de dieta por plan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving daily diets: " + e.getMessage()));
        }
    }

    /**
     * @deprecated Use {@link #createDiaDieta(DiaDietaDTO)} instead.
     */
    @Operation(summary = "Create daily diet (Legacy)", 
               description = "Creates a new daily diet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Daily diet created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save")
    @Deprecated
    public ResponseEntity<ManualApiResponseDTO<DiaDieta>> guardar(
            @Valid @RequestBody DiaDieta diaDieta) {
        log.debug("Método deprecado: guardar(DiaDieta)");
        try {
            DiaDieta nuevaDiaDieta = diaDietaService.insertar(diaDieta);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(nuevaDiaDieta, "Daily diet created successfully"));
        } catch (Exception e) {
            log.error("Error al guardar día de dieta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating daily diet: " + e.getMessage()));
        }
    }

    /**
     * @deprecated Use {@link #deleteDiaDieta(int)} instead.
     */
    @Operation(summary = "Delete daily diet (Legacy)", 
               description = "Deletes a daily diet by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Daily diet deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Daily diet not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/eliminar/{id}")
    @Deprecated
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminar(
            @Parameter(description = "Daily diet ID") @PathVariable int id) {
        log.debug("Método deprecado: eliminar({})", id);
        try {
            diaDietaService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Daily diet deleted successfully"));
        } catch (Exception e) {
            log.error("Error al eliminar día de dieta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting daily diet: " + e.getMessage()));
        }
    }
    
    // ============= Métodos Modernos =============
    
    /**
     * Obtiene todos los días de dieta.
     * 
     * @return ResponseEntity con la lista de días de dieta
     */
    @Operation(summary = "Get all daily diets", 
               description = "Retrieves all daily diets")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Daily diets retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<ManualApiResponseDTO<List<DiaDietaDTO>>> getAllDiaDietas() {
        log.debug("Obteniendo todos los días de dieta");
        try {
            List<DiaDietaDTO> diaDietas = diaDietaService.findAll();
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(diaDietas, "Daily diets retrieved successfully")
            );
        } catch (Exception e) {
            log.error("Error al obtener todos los días de dieta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving daily diets: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene un día de dieta por su ID.
     * 
     * @param id ID del día de dieta
     * @return ResponseEntity con el día de dieta
     */
    @Operation(summary = "Get daily diet by ID", 
               description = "Retrieves a specific daily diet by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Daily diet retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Daily diet not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/v1/{id}")
    public ResponseEntity<ManualApiResponseDTO<DiaDietaDTO>> getDiaDietaById(
            @Parameter(description = "Daily diet ID") @PathVariable int id) {
        log.debug("Obteniendo día de dieta con ID: {}", id);
        try {
            DiaDietaDTO diaDieta = diaDietaService.findById(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(diaDieta, "Daily diet retrieved successfully")
            );
        } catch (ResourceNotFoundException e) {
            log.debug("Día de dieta no encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener día de dieta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving daily diet: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene los días de dieta de un plan específico.
     * 
     * @param planId ID del plan de dieta
     * @return ResponseEntity con la lista de días de dieta
     */
    @Operation(summary = "Get daily diets by diet plan", 
               description = "Retrieves all daily diets for a specific diet plan")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Daily diets retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Diet plan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/v1/plan/{planId}")
    @PreAuthorize("hasAnyRole('NUTRICIONISTA', 'USER') and @securityService.canViewPlanDieta(#planId, authentication.name)")
    public ResponseEntity<ManualApiResponseDTO<List<DiaDietaDTO>>> getDiaDietasByPlanId(
            @Parameter(description = "Diet plan ID") @PathVariable int planId) {
        log.debug("Obteniendo días de dieta para plan con ID: {}", planId);
        try {
            List<DiaDietaDTO> diaDietas = diaDietaService.findByPlanId(planId);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(diaDietas, "Daily diets retrieved successfully")
            );
        } catch (ResourceNotFoundException e) {
            log.debug("Plan de dieta no encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al obtener días de dieta por plan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving daily diets: " + e.getMessage()));
        }
    }
    
    /**
     * Crea un nuevo día de dieta.
     * 
     * @param diaDietaDTO DTO del día de dieta a crear
     * @return ResponseEntity con el día de dieta creado
     */
    @Operation(summary = "Create daily diet", 
               description = "Creates a new daily diet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Daily diet created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/v1")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<ManualApiResponseDTO<DiaDietaDTO>> createDiaDieta(
            @Valid @RequestBody DiaDietaDTO diaDietaDTO) {
        log.debug("Creando día de dieta");
        try {
            DiaDietaDTO nuevaDiaDieta = diaDietaService.save(diaDietaDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(nuevaDiaDieta, "Daily diet created successfully"));
        } catch (Exception e) {
            log.error("Error al crear día de dieta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating daily diet: " + e.getMessage()));
        }
    }
    
    /**
     * Crea un nuevo día de dieta para un plan específico.
     * 
     * @param planId ID del plan de dieta
     * @param diaDietaDTO DTO del día de dieta a crear
     * @return ResponseEntity con el día de dieta creado
     */
    @Operation(summary = "Create daily diet for plan", 
               description = "Creates a new daily diet for a specific diet plan")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Daily diet created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Diet plan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/v1/plan/{planId}")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.canEditPlanDieta(#planId, authentication.name)")
    public ResponseEntity<ManualApiResponseDTO<DiaDietaDTO>> createDiaDietaForPlan(
            @Parameter(description = "Diet plan ID") @PathVariable int planId,
            @Valid @RequestBody DiaDietaDTO diaDietaDTO) {
        log.debug("Creando día de dieta para plan con ID: {}", planId);
        try {
            DiaDietaDTO nuevaDiaDieta = diaDietaService.saveForPlan(diaDietaDTO, planId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(nuevaDiaDieta, "Daily diet created successfully"));
        } catch (ResourceNotFoundException e) {
            log.debug("Plan de dieta no encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear día de dieta para plan: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating daily diet: " + e.getMessage()));
        }
    }
    
    /**
     * Actualiza un día de dieta existente.
     * 
     * @param id ID del día de dieta a actualizar
     * @param diaDietaDTO Nuevos datos del día de dieta
     * @return ResponseEntity con el día de dieta actualizado
     */
    @Operation(summary = "Update daily diet", 
               description = "Updates an existing daily diet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Daily diet updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Daily diet not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/v1/{id}")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.canEditDiaDieta(#id, authentication.name)")
    public ResponseEntity<ManualApiResponseDTO<DiaDietaDTO>> updateDiaDieta(
            @Parameter(description = "Daily diet ID") @PathVariable int id,
            @Valid @RequestBody DiaDietaDTO diaDietaDTO) {
        log.debug("Actualizando día de dieta con ID: {}", id);
        try {
            DiaDietaDTO updatedDiaDieta = diaDietaService.update(id, diaDietaDTO);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(updatedDiaDieta, "Daily diet updated successfully")
            );
        } catch (ResourceNotFoundException e) {
            log.debug("Día de dieta no encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar día de dieta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error updating daily diet: " + e.getMessage()));
        }
    }
    
    /**
     * Elimina un día de dieta.
     * 
     * @param id ID del día de dieta a eliminar
     * @return ResponseEntity sin contenido
     */
    @Operation(summary = "Delete daily diet", 
               description = "Deletes a daily diet by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Daily diet deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Daily diet not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/v1/{id}")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.canEditDiaDieta(#id, authentication.name)")
    public ResponseEntity<ManualApiResponseDTO<Void>> deleteDiaDieta(
            @Parameter(description = "Daily diet ID") @PathVariable int id) {
        log.debug("Eliminando día de dieta con ID: {}", id);
        try {
            diaDietaService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Daily diet deleted successfully"));
        } catch (ResourceNotFoundException e) {
            log.debug("Día de dieta no encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al eliminar día de dieta: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting daily diet: " + e.getMessage()));
        }
    }
}
