package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thunderfat.springboot.backend.validation.ValidationGroups;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Cita entities.
 * Follows Spring Boot 2025 best practices including:
 * - Comprehensive validation annotations
 * - Builder pattern for immutability options
 * - JSON formatting for API consistency
 * - Validation groups for different use cases
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO {
    
    /**
     * Unique identifier for the appointment.
     */
    private Integer id;
    
    /**
     * Appointment start date and time.
     * Must be in the future for new appointments.
     */
    @NotNull(message = "La fecha de inicio es obligatoria", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Future(message = "La fecha de inicio debe ser futura", groups = {ValidationGroups.Create.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaInicio;
    
    /**
     * Appointment end date and time.
     * Must be in the future for new appointments.
     */
    @NotNull(message = "La fecha de fin es obligatoria", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Future(message = "La fecha de fin debe ser futura", groups = {ValidationGroups.Create.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaFin;
    
    /**
     * Appointment notes or description.
     * Optional field with size constraints.
     */
    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notas;
    
    /**
     * Patient ID associated with this appointment.
     * Must be positive and not null for valid appointments.
     */
    @NotNull(message = "El ID del paciente es obligatorio", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Positive(message = "El ID del paciente debe ser positivo")
    private Integer pacienteId;
    
    /**
     * Nutritionist ID associated with this appointment.
     * Must be positive and not null for valid appointments.
     */
    @NotNull(message = "El ID del nutricionista es obligatorio", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Positive(message = "El ID del nutricionista debe ser positivo")
    private Integer nutricionistaId;
    
    // =====================================
    // COMPUTED FIELDS FOR API CONVENIENCE
    // =====================================
    
    /**
     * Patient full name for display purposes.
     * Computed field, not persisted.
     */
    private String pacienteNombre;
    
    /**
     * Nutritionist full name for display purposes.
     * Computed field, not persisted.
     */
    private String nutricionistaNombre;
    
    /**
     * Appointment duration in minutes.
     * Computed field based on start and end times.
     */
    private Long duracionMinutos;
    
    /**
     * Appointment status (upcoming, ongoing, completed).
     * Computed field based on current time and appointment times.
     */
    private String estado;
    
    // =====================================
    // BUSINESS HELPER METHODS
    // =====================================
    
    /**
     * Validates that the end time is after the start time.
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValidTimeRange() {
        if (fechaInicio == null || fechaFin == null) {
            return false;
        }
        return fechaFin.isAfter(fechaInicio);
    }
    
    /**
     * Calculates the duration of the appointment in minutes.
     * 
     * @return duration in minutes, or null if dates are invalid
     */
    public Long calculateDuration() {
        if (fechaInicio == null || fechaFin == null) {
            return null;
        }
        return java.time.Duration.between(fechaInicio, fechaFin).toMinutes();
    }
    
    /**
     * Determines the current status of the appointment.
     * 
     * @return status string (upcoming, ongoing, completed)
     */
    public String determineStatus() {
        if (fechaInicio == null || fechaFin == null) {
            return "unknown";
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isBefore(fechaInicio)) {
            return "upcoming";
        } else if (now.isAfter(fechaFin)) {
            return "completed";
        } else {
            return "ongoing";
        }
    }
    
    // Optional: Include nested DTOs for full object information when needed
    private PacienteDTO paciente;
    private NutricionistaDTO nutricionista;
}
