package com.thunderfat.springboot.backend.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.dto.CitaDTO;
import com.thunderfat.springboot.backend.model.service.ICitaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Citas", description = "Operations related to appointment management")
@CrossOrigin(origins={"http://localhost:4200","http://localhost:8100"})
@RestController
@RequestMapping(value="/cita")
public class CitaRestController {
    
    private final ICitaService citaService;
    
    public CitaRestController(ICitaService citaService) {
        this.citaService = citaService;
    }
    
    @Operation(summary = "Get all appointments", description = "Retrieves a list of all appointments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/todos")
    public ResponseEntity<ManualApiResponseDTO<List<CitaDTO>>> listarTodos() {
        try {
            List<CitaDTO> citas = citaService.listar();
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(citas, "Appointments retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving appointments: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Get appointment by ID", description = "Retrieves a specific appointment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointment retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<CitaDTO>> buscarPorId(
            @Parameter(description = "Appointment ID") @PathVariable int id) {
        try {
            CitaDTO cita = citaService.buscarPorId(id);
            if (cita != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(cita, "Appointment retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("Appointment not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving appointment: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Create appointment", description = "Creates a new appointment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Appointment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ManualApiResponseDTO<Void>> guardar(@Valid @RequestBody CitaDTO citaDTO) {
        try {
            citaService.insertar(citaDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(null, "Appointment created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error("Error creating appointment: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Delete appointment", description = "Deletes an appointment by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Appointment deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminar(
            @Parameter(description = "Appointment ID") @PathVariable int id) {
        try {
            citaService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(null, "Appointment deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error deleting appointment: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Get appointments by patient", description = "Retrieves all appointments for a specific patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<ManualApiResponseDTO<List<CitaDTO>>> listarPorPaciente(
            @Parameter(description = "Patient ID") @PathVariable int idPaciente) {
        try {
            List<CitaDTO> citas = citaService.buscarPorPaciente(idPaciente);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(citas, "Patient appointments retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving patient appointments: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Get appointments by nutritionist", description = "Retrieves all appointments for a specific nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/nutricionista/{idNutricionista}")
    public ResponseEntity<ManualApiResponseDTO<List<CitaDTO>>> listarPorNutricionista(
            @Parameter(description = "Nutritionist ID") @PathVariable int idNutricionista) {
        try {
            List<CitaDTO> citas = citaService.buscarPorNutricionista(idNutricionista);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(citas, "Nutritionist appointments retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving nutritionist appointments: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Get appointments by nutritionist between dates", 
               description = "Retrieves appointments for a nutritionist within a date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date range"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/citasNutricionista/{id}")
    public ResponseEntity<ManualApiResponseDTO<List<Map<String, Object>>>> getCitasPorNutricionistaEntreFechas(
            @Parameter(description = "Nutritionist ID") @PathVariable("id") int idNutricionista,
            @Parameter(description = "Start date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("start") LocalDate startDate,
            @Parameter(description = "End date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("end") LocalDate endDate) {
        try {
            List<Map<String, Object>> citas = citaService.listarPorNutricionistaEntreFechas(idNutricionista, startDate, endDate);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(citas, "Appointments in date range retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving appointments in date range: " + e.getMessage()));
        }
    }
    
    @Operation(summary = "Get next appointment for patient", 
               description = "Retrieves the next appointment for a patient from a given date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Next appointment retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No upcoming appointments found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/proximaCita/{idPaciente}")
    public ResponseEntity<ManualApiResponseDTO<CitaDTO>> proximaCita(
            @Parameter(description = "Patient ID") @PathVariable int idPaciente,
            @Parameter(description = "From date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("fechaDesde") LocalDate fechaDesde) {
        try {
            CitaDTO cita = citaService.buscarProximaCita(idPaciente, fechaDesde);
            if (cita != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(cita, "Next appointment retrieved successfully")
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error("No upcoming appointments found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error("Error retrieving next appointment: " + e.getMessage()));
        }
    }
}
