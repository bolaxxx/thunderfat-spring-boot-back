package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.model.dto.PacienteDTO;

/**
 * Service interface for managing Paciente entities.
 * Follows Spring Boot 2025 best practices with modern patterns and backward compatibility.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
public interface IPacienteService {
    
    // =====================================
    // MODERN METHODS (Spring Boot 2025)
    // =====================================
    
    /**
     * Retrieves all patients with pagination support.
     * 
     * @param pageable pagination information
     * @return paginated list of patients
     */
    Page<PacienteDTO> findAllPaginated(Pageable pageable);
    
    /**
     * Retrieves patients for a specific nutritionist with pagination.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated list of patients
     */
    Page<PacienteDTO> findByNutricionistaId(Integer nutricionistaId, Pageable pageable);
    
    /**
     * Finds a patient by ID using Optional pattern.
     * 
     * @param id the patient ID
     * @return optional containing the patient if found
     */
    Optional<PacienteDTO> findById(Integer id);
    
    /**
     * Creates a new patient.
     * 
     * @param pacienteDTO the patient data
     * @return the created patient with generated ID
     */
    PacienteDTO create(PacienteDTO pacienteDTO);
    
    /**
     * Updates an existing patient.
     * 
     * @param id the patient ID to update
     * @param pacienteDTO the updated patient data
     * @return the updated patient
     * @throws RuntimeException if patient not found
     */
    PacienteDTO update(Integer id, PacienteDTO pacienteDTO);
    
    /**
     * Deletes a patient by ID with proper relationship cleanup.
     * 
     * @param id the patient ID to delete
     * @throws RuntimeException if patient not found
     */
    void deleteById(Integer id);
    
    /**
     * Checks if a patient exists.
     * 
     * @param id the patient ID
     * @return true if patient exists
     */
    boolean existsById(Integer id);
    
    /**
     * Advanced search across multiple fields with pagination.
     * 
     * @param searchTerm the search term
     * @param nutricionistaId the nutritionist ID to limit search scope
     * @param pageable pagination information
     * @return paginated search results
     */
    Page<PacienteDTO> searchPatients(String searchTerm, Integer nutricionistaId, Pageable pageable);
    
    /**
     * Searches patients by DNI with pagination.
     * 
     * @param dni partial DNI to search for
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated search results
     */
    Page<PacienteDTO> findByDniContaining(String dni, Integer nutricionistaId, Pageable pageable);
    
    /**
     * Searches patients by phone number with pagination.
     * 
     * @param telefono partial phone number to search for
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated search results
     */
    Page<PacienteDTO> findByTelefonoContaining(String telefono, Integer nutricionistaId, Pageable pageable);
    
    /**
     * Searches patients by full name with pagination.
     * 
     * @param nombres the name to search for
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated search results
     */
    Page<PacienteDTO> findByFullNameContaining(String nombres, Integer nutricionistaId, Pageable pageable);
    
    /**
     * Finds patients with appointments in a specific date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated list of patients with appointments in date range
     */
    Page<PacienteDTO> findWithAppointmentsBetweenDates(LocalDate startDate, LocalDate endDate, 
                                                      Integer nutricionistaId, Pageable pageable);
    
    /**
     * Counts active patients for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return count of active patients
     */
    Long countActivePatientsByNutricionistaId(Integer nutricionistaId);
    
    /**
     * Validates if a patient belongs to a specific nutritionist.
     * 
     * @param pacienteId the patient ID
     * @param nutricionistaId the nutritionist ID
     * @return true if the patient belongs to the nutritionist
     */
    boolean belongsToNutritionist(Integer pacienteId, Integer nutricionistaId);
    
    /**
     * Finds a patient by email.
     * 
     * @param email the patient's email
     * @return optional containing the patient if found
     */
    Optional<PacienteDTO> findByEmail(String email);
    
    /**
     * Validates unique constraints (email, DNI) for new patients.
     * 
     * @param pacienteDTO the patient data to validate
     * @return true if all unique constraints pass
     */
    boolean validateUniqueConstraints(PacienteDTO pacienteDTO);
    
    /**
     * Validates unique constraints for patient updates.
     * 
     * @param id the patient ID being updated
     * @param pacienteDTO the updated patient data
     * @return true if all unique constraints pass
     */
    boolean validateUniqueConstraintsForUpdate(Integer id, PacienteDTO pacienteDTO);
    
    // =====================================
    // LEGACY METHODS (Backward Compatibility)
    // =====================================
    
    /**
     * @deprecated Use {@link #findAllPaginated(Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    List<PacienteDTO> ListarPaciente();
    
    /**
     * @deprecated Use {@link #create(PacienteDTO)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    void insertar(PacienteDTO paciente);
    
    /**
     * @deprecated Use {@link #findById(Integer)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    PacienteDTO buscarPorId(int id_paciente);
    
    /**
     * @deprecated Use {@link #deleteById(Integer)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    void eliminar(int id_paciente);
    
    /**
     * @deprecated Use {@link #findByNutricionistaId(Integer, Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    List<PacienteDTO> listarPacienteNutrcionista(int id_nutricionista);
    
    /**
     * @deprecated Use {@link #searchPatients(String, Integer, Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    List<PacienteDTO> buscarNombreCompleto(int id, String searchterm);
    
    /**
     * @deprecated Use {@link #findByDniContaining(String, Integer, Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    List<PacienteDTO> buscarPorDni(int id, String Dni);
    
    /**
     * @deprecated Use {@link #findByTelefonoContaining(String, Integer, Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    List<PacienteDTO> buscarPorTelefono(String email, int id);
}
