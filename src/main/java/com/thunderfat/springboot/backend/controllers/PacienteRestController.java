package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.service.IPacienteService;

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

@Tag(name = "Patients", description = "Operations related to patient management")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/paciente")
public class PacienteRestController {
    
    private final IPacienteService pacienteService;

    public PacienteRestController(IPacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @Operation(summary = "Get all patients", 
               description = "Retrieves a list of all patients in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patients retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/todos")
    public ResponseEntity<ManualApiResponseDTO<List<PacienteDTO>>> listarTodos() {
        try {
            List<PacienteDTO> pacientes = pacienteService.ListarPaciente();
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(pacientes, "All patients retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving patients: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get patients by nutritionist", 
               description = "Retrieves all patients assigned to a specific nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patients retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Nutritionist not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<PacienteDTO>>> listarPorNutricionista(
            @Parameter(description = "Nutritionist ID") @PathVariable int id) {
        try {
            List<PacienteDTO> pacientes = pacienteService.listarPacienteNutrcionista(id);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(pacientes, "Patients for nutritionist retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving patients for nutritionist: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get patient details", 
               description = "Retrieves detailed information of a specific patient by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/detalle/{id}")
    public ResponseEntity<ManualApiResponseDTO<PacienteDTO>> buscarPorId(
            @Parameter(description = "Patient ID") @PathVariable int id) {
        try {
            PacienteDTO paciente = pacienteService.buscarPorId(id);
            if (paciente != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(paciente, "Patient retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Patient not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving patient: " + e.getMessage()));
        }
    }

    @Operation(summary = "Create patient", 
               description = "Creates a new patient in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Patient created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save")
    public ResponseEntity<ManualApiResponseDTO<Void>> guardarPaciente(
            @Valid @RequestBody PacienteDTO newPacienteDTO) {
        try {
            pacienteService.insertar(newPacienteDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(null, "Patient created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating patient: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete patient", 
               description = "Deletes a patient from the system by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminarPorId(
            @Parameter(description = "Patient ID") @PathVariable int id) {
        try {
            pacienteService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Patient deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting patient: " + e.getMessage()));
        }
    }

    @Operation(summary = "Search patients by full name", 
               description = "Searches for patients by their full name within a nutritionist's patient list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/searchFullName")
    public ResponseEntity<ManualApiResponseDTO<List<PacienteDTO>>> buscarPorNombreCompleto(
            @Parameter(description = "Nutritionist ID") @RequestParam("id") int idNutricionista, 
            @Parameter(description = "Search term") @RequestParam("searchterm") String searchTerm) {
        try {
            List<PacienteDTO> pacientes = pacienteService.buscarNombreCompleto(idNutricionista, searchTerm);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(pacientes, "Patients found by full name")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error searching patients by full name: " + e.getMessage()));
        }
    }

    @Operation(summary = "Search patients by DNI", 
               description = "Searches for patients by their DNI within a nutritionist's patient list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/searchDni")
    public ResponseEntity<ManualApiResponseDTO<List<PacienteDTO>>> buscarPorDni(
            @Parameter(description = "Nutritionist ID") @RequestParam("id") int idNutricionista, 
            @Parameter(description = "DNI search term") @RequestParam("searchterm") String searchTerm) {
        try {
            List<PacienteDTO> pacientes = pacienteService.buscarPorDni(idNutricionista, searchTerm);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(pacientes, "Patients found by DNI")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error searching patients by DNI: " + e.getMessage()));
        }
    }

    @Operation(summary = "Search patients by phone", 
               description = "Searches for patients by their phone number within a nutritionist's patient list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/searchPhone")
    public ResponseEntity<ManualApiResponseDTO<List<PacienteDTO>>> buscarPorTelefono(
            @Parameter(description = "Nutritionist ID") @RequestParam("id") int idNutricionista, 
            @Parameter(description = "Phone search term") @RequestParam("searchterm") String searchTerm) {
        try {
            List<PacienteDTO> pacientes = pacienteService.buscarPorTelefono(searchTerm, idNutricionista);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(pacientes, "Patients found by phone")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error searching patients by phone: " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete patient (alternative endpoint)", 
               description = "Deletes a patient from the system with enhanced error handling")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "500", description = "Database error occurred")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> borrarPaciente(
            @Parameter(description = "Patient ID") @PathVariable("id") int idPaciente) {
        try {
            pacienteService.eliminar(idPaciente);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(null, "Patient deleted successfully")
            );
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Database error while deleting patient: " + e.getMostSpecificCause().getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting patient: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update patient", 
               description = "Updates an existing patient's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ManualApiResponseDTO<PacienteDTO>> actualizar(
            @Parameter(description = "Patient ID") @PathVariable("id") int idPaciente, 
            @Valid @RequestBody PacienteDTO paciente) {
        try {
            PacienteDTO pacienteActual = pacienteService.buscarPorId(idPaciente);
            if (pacienteActual == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ManualApiResponseDTO.error("Patient not found"));
            }
            
            // Update patient data
            pacienteActual.setAltura(paciente.getAltura());
            pacienteActual.setApellidos(paciente.getApellidos());
            pacienteActual.setEmail(paciente.getEmail());
            pacienteActual.setCodigopostal(paciente.getCodigopostal());
            pacienteActual.setNombre(paciente.getNombre());
            pacienteActual.setDireccion(paciente.getDireccion());
            pacienteActual.setDni(paciente.getDni());
            pacienteActual.setSexo(paciente.getSexo());
            pacienteActual.setLocalidad(paciente.getLocalidad());
            pacienteActual.setProvincia(paciente.getProvincia());
            pacienteActual.setFechanacimiento(paciente.getFechanacimiento());
            pacienteActual.setTelefono(paciente.getTelefono());
            
            pacienteService.insertar(pacienteActual);
            
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(pacienteActual, "Patient updated successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error updating patient: " + e.getMessage()));
        }
    }
}
