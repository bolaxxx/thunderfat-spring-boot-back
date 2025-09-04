package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.model.dto.CitaDTO;

/**
 * Service interface for managing Cita (Appointment) entities.
 * Follows Spring Boot 2025 best practices with modern patterns and backward compatibility.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
public interface ICitaService {
    
    // =====================================
    // MODERN METHODS (Spring Boot 2025)
    // =====================================
    
    /**
     * Retrieves all appointments with pagination support.
     * 
     * @param pageable pagination information
     * @return paginated list of appointments
     */
    Page<CitaDTO> findAllPaginated(Pageable pageable);
    
    /**
     * Finds an appointment by ID using Optional pattern.
     * 
     * @param id the appointment ID
     * @return optional containing the appointment if found
     */
    Optional<CitaDTO> findById(Integer id);
    
    /**
     * Creates a new appointment.
     * 
     * @param citaDTO the appointment data
     * @return the created appointment with generated ID
     */
    CitaDTO create(CitaDTO citaDTO);
    
    /**
     * Updates an existing appointment.
     * 
     * @param id the appointment ID to update
     * @param citaDTO the updated appointment data
     * @return the updated appointment
     * @throws RuntimeException if appointment not found
     */
    CitaDTO update(Integer id, CitaDTO citaDTO);
    
    /**
     * Deletes an appointment by ID.
     * 
     * @param id the appointment ID to delete
     * @throws RuntimeException if appointment not found
     */
    void deleteById(Integer id);
    
    /**
     * Checks if an appointment exists.
     * 
     * @param id the appointment ID
     * @return true if appointment exists
     */
    boolean existsById(Integer id);
    
    /**
     * Finds appointments for a specific patient with pagination.
     * 
     * @param pacienteId the patient ID
     * @param pageable pagination information
     * @return paginated list of patient appointments
     */
    Page<CitaDTO> findByPacienteId(Integer pacienteId, Pageable pageable);
    
    /**
     * Finds appointments for a specific nutritionist with pagination.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated list of nutritionist appointments
     */
    Page<CitaDTO> findByNutricionistaId(Integer nutricionistaId, Pageable pageable);
    
    /**
     * Finds appointments for a nutritionist within a date range with pagination.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return paginated list of appointments in date range
     */
    Page<CitaDTO> findByNutricionistaIdAndDateRange(Integer nutricionistaId, LocalDate startDate, 
                                                   LocalDate endDate, Pageable pageable);
    
    /**
     * Finds appointments within a specific date range with pagination.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return paginated list of appointments in date range
     */
    Page<CitaDTO> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Finds the next upcoming appointment for a patient.
     * 
     * @param pacienteId the patient ID
     * @param fromDate the date to search from (default: today)
     * @return optional containing the next appointment if found
     */
    Optional<CitaDTO> findNextAppointmentForPatient(Integer pacienteId, LocalDate fromDate);
    
    /**
     * Finds upcoming appointments for a nutritionist within specified days.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param daysAhead number of days to look ahead
     * @param pageable pagination information
     * @return paginated list of upcoming appointments
     */
    Page<CitaDTO> findUpcomingAppointments(Integer nutricionistaId, Integer daysAhead, Pageable pageable);
    
    /**
     * Finds conflicting appointments for scheduling validation.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDateTime the proposed start time
     * @param endDateTime the proposed end time
     * @param excludeAppointmentId optional appointment ID to exclude (for updates)
     * @return list of conflicting appointments
     */
    List<CitaDTO> findConflictingAppointments(Integer nutricionistaId, LocalDateTime startDateTime, 
                                            LocalDateTime endDateTime, Integer excludeAppointmentId);
    
    /**
     * Counts total appointments for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return total count of appointments
     */
    Long countAppointmentsByNutricionistaId(Integer nutricionistaId);
    
    /**
     * Counts appointments for a patient.
     * 
     * @param pacienteId the patient ID
     * @return total count of patient appointments
     */
    Long countAppointmentsByPacienteId(Integer pacienteId);
    
    /**
     * Gets appointment statistics for a nutritionist within date range.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDate the start date
     * @param endDate the end date
     * @return map containing appointment statistics
     */
    Map<String, Object> getAppointmentStatistics(Integer nutricionistaId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Validates appointment scheduling business rules.
     * 
     * @param citaDTO the appointment to validate
     * @return true if appointment can be scheduled
     */
    boolean validateAppointmentScheduling(CitaDTO citaDTO);
    
    /**
     * Gets calendar events for a nutritionist (optimized for calendar display).
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of calendar event maps
     */
    List<Map<String, Object>> getCalendarEvents(Integer nutricionistaId, LocalDate startDate, LocalDate endDate);
    
    // =====================================
    // LEGACY METHODS (Backward Compatibility)
    // =====================================
    
    /**
     * @deprecated Use {@link #findAllPaginated(Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    List<CitaDTO> listar();
    
    /**
     * @deprecated Use {@link #findById(Integer)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    CitaDTO buscarPorId(int id);
    
    /**
     * @deprecated Use {@link #create(CitaDTO)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    void insertar(CitaDTO citaDTO);
    
    /**
     * @deprecated Use {@link #deleteById(Integer)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    void eliminar(int id);
    
    /**
     * @deprecated Use {@link #findByPacienteId(Integer, Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    List<CitaDTO> buscarPorPaciente(int idPaciente);
    
    /**
     * @deprecated Use {@link #findByNutricionistaId(Integer, Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    List<CitaDTO> buscarPorNutricionista(int idNutricionista);
    
    /**
     * @deprecated Use {@link #getCalendarEvents(Integer, LocalDate, LocalDate)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    List<Map<String, Object>> listarPorNutricionistaEntreFechas(int idNutricionista, LocalDate start, LocalDate end);
    
    /**
     * @deprecated Use {@link #findNextAppointmentForPatient(Integer, LocalDate)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    CitaDTO buscarProximaCita(int idPaciente, LocalDate fechaDesde);
}
