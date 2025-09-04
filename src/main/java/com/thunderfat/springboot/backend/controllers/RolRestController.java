package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.dto.RolDTO;
import com.thunderfat.springboot.backend.model.service.IRolService;

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

@Tag(name = "Role", description = "Operations related to role management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/rol")
public class RolRestController {
    
    private final IRolService rolService;

    public RolRestController(IRolService rolService) {
        this.rolService = rolService;
    }

    @Operation(summary = "Get all roles", 
               description = "Retrieves all roles in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Roles retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/todos")
    public ResponseEntity<ManualApiResponseDTO<List<RolDTO>>> listarTodos() {
        try {
            List<RolDTO> roles = rolService.listar();
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(roles, "Roles retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving roles: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get role by ID", 
               description = "Retrieves a specific role by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<RolDTO>> buscarPorId(
            @Parameter(description = "Role ID") @PathVariable int id) {
        try {
            RolDTO rol = rolService.buscarPorId(id);
            if (rol != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(rol, "Role retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Role not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving role: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create role", 
               description = "Creates a new role in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save")
    public ResponseEntity<ManualApiResponseDTO<RolDTO>> guardar(
            @Valid @RequestBody RolDTO rolDTO) {
        try {
            rolService.insertar(rolDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(rolDTO, "Role created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating role: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update role", 
               description = "Updates an existing role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ManualApiResponseDTO<RolDTO>> actualizar(
            @Parameter(description = "Role ID") @PathVariable int id,
            @Valid @RequestBody RolDTO rolDTO) {
        try {
            rolDTO.setId(id); // Ensure the ID is set
            rolService.insertar(rolDTO); // Assuming insertar handles updates
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(rolDTO, "Role updated successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error updating role: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete role", 
               description = "Deletes a role by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminar(
            @Parameter(description = "Role ID") @PathVariable int id) {
        try {
            rolService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Role deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting role: " + e.getMessage()));
        }
    }
}
