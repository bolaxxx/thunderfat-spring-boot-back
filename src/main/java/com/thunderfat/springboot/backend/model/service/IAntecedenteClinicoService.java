package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.model.dto.AntecedentesClinicosDTO;


/**
 * Modern Spring Boot 2025 service interface for AntecedentesClinicos (Clinical History).
 * Provides comprehensive clinical history management functionality for patients.
 * 
 * Features:
 * - Legacy method support for backward compatibility
 * - Modern DTO-based operations with pagination
 * - Specialized medical condition queries
 * - Performance optimized with caching
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
public interface IAntecedenteClinicoService {
	

    // ================================
    // MODERN DTO-BASED OPERATIONS
    // ================================
    
    /**
     * Get all clinical antecedents with pagination
     * @param pageable Pagination information
     * @return Page of clinical antecedent DTOs
     */
    Page<AntecedentesClinicosDTO> findAll(Pageable pageable);
    
    /**
     * Find clinical antecedent by ID
     * @param id Clinical antecedent ID
     * @return Optional clinical antecedent DTO
     */
    Optional<AntecedentesClinicosDTO> findById(Integer id);
    
    /**
     * Save or update a clinical antecedent
     * @param antecedenteDTO Clinical antecedent data
     * @return Saved clinical antecedent DTO
     */
    AntecedentesClinicosDTO save(AntecedentesClinicosDTO antecedenteDTO);
    
    /**
     * Delete clinical antecedent by ID
     * @param id Clinical antecedent ID
     */
    void deleteById(Integer id);
    
    // ================================
    // BUSINESS-SPECIFIC OPERATIONS
    // ================================
    
    /**
     * Find clinical antecedents by patient ID
     * @param pacienteId Patient ID
     * @return List of clinical antecedent DTOs
     */
    List<AntecedentesClinicosDTO> findByPacienteId(Integer pacienteId);
    
    /**
     * Find clinical antecedents by patient ID with pagination
     * @param pacienteId Patient ID
     * @param pageable Pagination information
     * @return Page of clinical antecedent DTOs
     */
    Page<AntecedentesClinicosDTO> findByPacienteId(Integer pacienteId, Pageable pageable);
    
    /**
     * Find allergies for a patient
     * @param pacienteId Patient ID
     * @return List of allergy-related clinical antecedent DTOs
     */
    List<AntecedentesClinicosDTO> findAlergiasByPacienteId(Integer pacienteId);
    
    /**
     * Find cardiovascular conditions for a patient
     * @param pacienteId Patient ID
     * @return List of cardiovascular-related clinical antecedent DTOs
     */
    List<AntecedentesClinicosDTO> findCondicionesCardiovascularByPacienteId(Integer pacienteId);
    
    /**
     * Find diabetes-related conditions for a patient
     * @param pacienteId Patient ID
     * @return List of diabetes-related clinical antecedent DTOs
     */
    List<AntecedentesClinicosDTO> findCondicionesDiabeticasByPacienteId(Integer pacienteId);
    
    /**
     * Find gastrointestinal conditions for a patient
     * @param pacienteId Patient ID
     * @return List of gastrointestinal-related clinical antecedent DTOs
     */
    List<AntecedentesClinicosDTO> findCondicionesGastrointestinalByPacienteId(Integer pacienteId);
    
    /**
     * Count clinical antecedents for a patient
     * @param pacienteId Patient ID
     * @return Count of clinical antecedents
     */
    Long countByPacienteId(Integer pacienteId);
}
