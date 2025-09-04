package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;
import com.thunderfat.springboot.backend.model.service.IPlatoPredetereminadoService;
import com.thunderfat.springboot.backend.model.service.PlatoPredeterminadoJPA;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Predetermined Dishes", description = "Operations related to predetermined dish management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/platopredeterminado")
public class PlatoPredeterminadoRestController {
    
    private final IPlatoPredetereminadoService servicefiltro;
    
    // Field for modern PlatoPredeterminadoJPA implementation
    private final PlatoPredeterminadoJPA modernService;

    public PlatoPredeterminadoRestController(IPlatoPredetereminadoService servicefiltro, PlatoPredeterminadoJPA modernService) {
        this.servicefiltro = servicefiltro;
        this.modernService = modernService;
    }

    @Operation(summary = "Get predetermined dishes by nutritionist", 
               description = "Retrieves all predetermined dishes created by a specific nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dishes retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Nutritionist not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<PlatoPredeterminado>>> listarporNutriconista(
            @Parameter(description = "Nutritionist ID") @PathVariable int id) {
        try {
            List<PlatoPredeterminado> platos = servicefiltro.listarPorNutricionista(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(platos, "Predetermined dishes retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving predetermined dishes: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get predetermined dish details", 
               description = "Retrieves detailed information of a specific predetermined dish by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dish retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Dish not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ManualApiResponseDTO<PlatoPredeterminado>> buscaPorId(
            @Parameter(description = "Predetermined dish ID") @PathVariable int id) {
        try {
            PlatoPredeterminado plato = servicefiltro.buscarPorId(id);
            if (plato != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(plato, "Predetermined dish retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Predetermined dish not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving predetermined dish: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create predetermined dish", 
               description = "Creates a new predetermined dish for a nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dish created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save/{id}")
    public ResponseEntity<ManualApiResponseDTO<PlatoPredeterminado>> guarPaciente(
            @Valid @RequestBody PlatoPredeterminado newPaciente, 
            @Parameter(description = "Nutritionist ID") @PathVariable("id") int id_nutricionista) {
        try {
            PlatoPredeterminado plato = servicefiltro.insertar(newPaciente, id_nutricionista);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(plato, "Predetermined dish created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating predetermined dish: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete predetermined dish", 
               description = "Deletes a predetermined dish by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Dish deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Dish not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminarPorId(
            @Parameter(description = "Predetermined dish ID") @PathVariable int id) {
        try {
            servicefiltro.eliminarPlatoPredeterminado(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Predetermined dish deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting predetermined dish: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update predetermined dish", 
               description = "Updates an existing predetermined dish (placeholder implementation)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dish updated successfully"),
        @ApiResponse(responseCode = "404", description = "Dish not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> actualiar(
            @Parameter(description = "Dish ID") @PathVariable("id") int id_paciente, 
            @Valid @RequestBody PlatoPredeterminado paciente) {
        try {
            // Update logic placeholder - implementation needed
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(null, "Predetermined dish update functionality not implemented")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error updating predetermined dish: " + e.getMessage()));
        }
    }
    
    // =====================================
    // MODERN ENDPOINTS (Spring Boot 2025)
    // =====================================
    
    @Operation(summary = "Get predetermined dish by ID (Modern)", 
               description = "Retrieves a predetermined dish by its ID using DTO pattern")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dish retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Dish not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/v1/dishes/{id}")
    public ResponseEntity<ManualApiResponseDTO<PlatoPredeterminadoDTO>> getDishById(
            @Parameter(description = "Dish ID") @PathVariable Integer id) {
        try {
            return modernService.findById(id)
                .map(dto -> ResponseEntity.ok(ManualApiResponseDTO.success(dto, "Dish retrieved successfully")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ManualApiResponseDTO.error("Dish not found with id: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving dish: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "List dishes by nutritionist (Modern)", 
               description = "Retrieves all dishes for a nutritionist with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dishes retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/api/v1/nutritionists/{nutricionistaId}/dishes")
    public ResponseEntity<ManualApiResponseDTO<List<PlatoPredeterminadoDTO>>> getDishesByNutritionist(
            @Parameter(description = "Nutritionist ID") @PathVariable Integer nutricionistaId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        try {
            List<PlatoPredeterminadoDTO> dishes = modernService.findAllByNutricionistaId(nutricionistaId);
            return ResponseEntity.ok(ManualApiResponseDTO.success(dishes, "Dishes retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving dishes: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Create dish (Modern)", 
               description = "Creates a new predetermined dish using DTO pattern")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dish created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/api/v1/nutritionists/{nutricionistaId}/dishes")
    public ResponseEntity<ManualApiResponseDTO<PlatoPredeterminadoDTO>> createDish(
            @Parameter(description = "Nutritionist ID") @PathVariable Integer nutricionistaId,
            @Valid @RequestBody PlatoPredeterminadoDTO platoPredeterminadoDTO) {
        try {
            PlatoPredeterminadoDTO created = modernService.create(platoPredeterminadoDTO, nutricionistaId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(created, "Dish created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating dish: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Delete dish (Modern)", 
               description = "Deletes a predetermined dish using modern pattern")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Dish deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Dish not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/api/v1/dishes/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> deleteDish(
            @Parameter(description = "Dish ID") @PathVariable Integer id) {
        try {
            modernService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Dish deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting dish: " + e.getMessage()));
        }
    }
}
