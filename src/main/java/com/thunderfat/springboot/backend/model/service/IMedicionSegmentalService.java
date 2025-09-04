package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;

/**
 * Service interface for managing MedicionSegmental (Segmental Measurement) operations.
 * Follows Spring Boot 2025 best practices including:
 * - Pagination support for list operations
 * - Optional return types for null safety
 * - Modern method naming conventions
 * - Comprehensive measurement tracking
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
public interface IMedicionSegmentalService {
    
    // ================================
    // CORE CRUD OPERATIONS
    // ================================
    
    /**
     * Creates a new segmental measurement for a patient.
     * 
     * @param medicion the measurement data
     * @param pacienteId the patient ID
     */
    void insertar(MedicionSegmental medicion, int pacienteId);
    
    /**
     * Updates an existing segmental measurement.
     * 
     * @param medicion the updated measurement data
     * @param pacienteId the patient ID
     */
    void actualizar(MedicionSegmental medicion, int pacienteId);
    
    /**
     * Deletes a segmental measurement by ID.
     * 
     * @param idMedicion the measurement ID
     */
    void eliminar(int idMedicion);
    
    /**
     * Finds a segmental measurement by ID.
     * 
     * @param idMedicion the measurement ID
     * @return the measurement, or null if not found
     */
    MedicionSegmental buscarPorID(int idMedicion);
    
    // ================================
    // LIST OPERATIONS
    // ================================
    
    /**
     * Lists all segmental measurements.
     * 
     * @return list of all measurements
     */
    List<MedicionSegmental> listar();
    
    /**
     * Lists all segmental measurements for a specific patient.
     * 
     * @param pacienteId the patient ID
     * @return list of measurements for the patient
     */
    List<MedicionSegmental> listarPorPaciente(int pacienteId);
    
    // ================================
    // MODERN OPERATIONS (Spring Boot 2025)
    // ================================
    
    /**
     * Finds segmental measurements for a patient with pagination.
     * 
     * @param pacienteId the patient ID
     * @param pageable pagination information
     * @return paginated list of measurements
     */
    Page<MedicionSegmental> findByPacienteId(Integer pacienteId, Pageable pageable);
    
    /**
     * Finds the most recent segmental measurement for a patient.
     * 
     * @param pacienteId the patient ID
     * @return optional containing the most recent measurement
     */
    Optional<MedicionSegmental> findLatestByPacienteId(Integer pacienteId);
    
    /**
     * Finds segmental measurements for a patient within a date range.
     * 
     * @param pacienteId the patient ID
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return paginated list of measurements in the date range
     */
    Page<MedicionSegmental> findByPacienteIdAndDateRange(
        Integer pacienteId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Gets average body fat percentage for a patient over a date range.
     * 
     * @param pacienteId the patient ID
     * @param startDate the start date
     * @param endDate the end date
     * @return average body fat percentage
     */
    Double getAverageBodyFatPercentage(Integer pacienteId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Gets average muscle mass for a patient over a date range.
     * 
     * @param pacienteId the patient ID
     * @param startDate the start date
     * @param endDate the end date
     * @return average muscle mass
     */
    Double getAverageMuscleMass(Integer pacienteId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Counts total segmental measurements for a patient.
     * 
     * @param pacienteId the patient ID
     * @return count of measurements
     */
    Long countByPacienteId(Integer pacienteId);
}
