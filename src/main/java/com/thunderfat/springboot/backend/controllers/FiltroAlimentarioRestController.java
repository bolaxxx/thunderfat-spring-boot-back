package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;
import com.thunderfat.springboot.backend.model.service.IFiltroAlimentarioService;

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

@Tag(name = "Filtros Alimentarios", description = "Operations related to dietary filter management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/filtroAlimentario")
public class FiltroAlimentarioRestController {
    
    private final IFiltroAlimentarioService serviceFiltro;

    public FiltroAlimentarioRestController(IFiltroAlimentarioService serviceFiltro) {
        this.serviceFiltro = serviceFiltro;
    }

    @Operation(summary = "Get dietary filters by nutritionist", 
               description = "Retrieves all dietary filters for a specific nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dietary filters retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<FiltroAlimentario>>> listarPorNutricionista(
            @Parameter(description = "Nutritionist ID") @PathVariable int id) {
        try {
            List<FiltroAlimentario> filtros = serviceFiltro.listarporNutricionista(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(filtros, "Dietary filters retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving dietary filters: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get dietary filter by ID", 
               description = "Retrieves a specific dietary filter by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dietary filter retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Dietary filter not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ManualApiResponseDTO<FiltroAlimentario>> buscarPorId(
            @Parameter(description = "Filter ID") @PathVariable int id) {
        try {
            FiltroAlimentario filtro = serviceFiltro.buscarPorId(id);
            if (filtro != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(filtro, "Dietary filter retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Dietary filter not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving dietary filter: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create dietary filter", 
               description = "Creates a new dietary filter for a nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dietary filter created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<FiltroAlimentario>> guardarFiltroAlimentario(
            @Valid @RequestBody FiltroAlimentario newFiltro,
            @Parameter(description = "Nutritionist ID") @PathVariable("id") int idNutricionista) {
        try {
            serviceFiltro.insertar(newFiltro, idNutricionista);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(newFiltro, "Dietary filter created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating dietary filter: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update dietary filter", 
               description = "Updates an existing dietary filter by deleting and recreating it")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dietary filter updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Dietary filter not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> actualizarFiltroAlimentario(
            @Parameter(description = "Filter ID") @PathVariable("id") int idFiltro,
            @Parameter(description = "Nutritionist ID") @RequestParam int idNutricionista,
            @Valid @RequestBody FiltroAlimentario filtro) {
        try {
            // Since there's no update method, we delete and recreate
            serviceFiltro.eliminar(idFiltro);
            serviceFiltro.insertar(filtro, idNutricionista);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(null, "Dietary filter updated successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error updating dietary filter: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete dietary filter", 
               description = "Deletes a dietary filter by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Dietary filter deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Dietary filter not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminarPorId(
            @Parameter(description = "Filter ID") @PathVariable int id) {
        try {
            serviceFiltro.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Dietary filter deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting dietary filter: " + e.getMessage()));
        }
    }
}
