package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.service.INutricionistaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Nutritionist", description = "Operations related to nutritionist management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/nutricionista")
public class NutricionistaRestController {
    @Operation(summary = "Get nutritionists by search term", 
               description = "Retrieves nutritionists matching a search term across name, email, phone, and professional registration number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nutritionists retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No nutritionists found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<ManualApiResponseDTO<List<Nutricionista>>> searchNutricionistas(
            @Parameter(description = "Search term") @RequestParam String searchTerm) {
        try {
            List<Nutricionista> nutricionistas = nutricionistaService.searchNutricionistas(searchTerm);
            if (nutricionistas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ManualApiResponseDTO.error("No nutritionists found"));
            }
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(nutricionistas, "Nutritionists retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error searching nutritionists: " + e.getMessage()));
        }
    }
    
    private final INutricionistaService nutricionistaService;

    public NutricionistaRestController(INutricionistaService nutricionistaService) {
        this.nutricionistaService = nutricionistaService;
    }

    @Operation(summary = "Get all nutritionists", 
               description = "Retrieves all nutritionists in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nutritionists retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/todos")
    public ResponseEntity<ManualApiResponseDTO<List<Nutricionista>>> listarTodos() {
        try {
            List<Nutricionista> nutricionistas = nutricionistaService.listarNutricionista();
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(nutricionistas, "Nutritionists retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving nutritionists: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get nutritionist by ID", 
               description = "Retrieves a specific nutritionist by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nutritionist retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Nutritionist not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Nutricionista>> buscarPorId(
            @Parameter(description = "Nutritionist ID") @PathVariable int id) {
        try {
            Nutricionista nutricionista = nutricionistaService.buscarPorId(id);
            if (nutricionista != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(nutricionista, "Nutritionist retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Nutritionist not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving nutritionist: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get available provinces", 
               description = "Retrieves all provinces where nutritionists are available")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Provinces retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/provincias")
    public ResponseEntity<ManualApiResponseDTO<List<String>>> buscarProvincias() {
        try {
            List<String> provincias = nutricionistaService.buscarProvincias();
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(provincias, "Provinces retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving provinces: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get localities by province", 
               description = "Retrieves all localities in a specific province where nutritionists are available")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Localities retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Province not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/localidades/{provincia}")
    public ResponseEntity<ManualApiResponseDTO<List<String>>> buscarLocalidadesPorProvincia(
            @Parameter(description = "Province name") @PathVariable String provincia) {
        try {
            List<String> localidades = nutricionistaService.buscarLocalidadesporProvincia(provincia);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(localidades, "Localities retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving localities: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get nutritionists by locality", 
               description = "Retrieves all nutritionists in a specific locality")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nutritionists retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Locality not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/localidad/{localidad}")
    public ResponseEntity<ManualApiResponseDTO<List<Nutricionista>>> listarPorLocalidad(
            @Parameter(description = "Locality name") @PathVariable String localidad) {
        try {
            List<Nutricionista> nutricionistas = nutricionistaService.listarNutricionistaporlocalidad(localidad);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(nutricionistas, "Nutritionists retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving nutritionists: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete nutritionist", 
               description = "Deletes a nutritionist from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Nutritionist deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Nutritionist not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminar(
            @Parameter(description = "Nutritionist ID") @PathVariable int id) {
        try {
            Nutricionista nutricionista = nutricionistaService.buscarPorId(id);
            if (nutricionista != null) {
                nutricionistaService.eliminar(nutricionista);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(ManualApiResponseDTO.success(null, "Nutritionist deleted successfully"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Nutritionist not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting nutritionist: " + e.getMessage()));
        }
    }
}
