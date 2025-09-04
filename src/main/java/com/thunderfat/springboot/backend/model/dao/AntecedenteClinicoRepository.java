package com.thunderfat.springboot.backend.model.dao;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;

/**
 * Repository interface for managing AntecedentesClinicos entities.
 * Handles clinical history and medical background information for patients.
 * Provides CRUD operations and specialized queries for medical condition tracking,
 * allergy management, and comprehensive patient health history analysis.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 2025
 */
@Repository
public interface AntecedenteClinicoRepository extends BaseRepository<AntecedentesClinicos, Integer> {

    /**
     * Finds all clinical antecedents for a specific patient.
     * 
     * @param pacienteId the ID of the patient
     * @return list of clinical antecedents for the patient
     */
    @Query("SELECT ac FROM AntecedentesClinicos ac WHERE ac.paciente.id = :pacienteId ORDER BY ac.paciente.id DESC")
    List<AntecedentesClinicos> findByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds clinical antecedents for a patient with pagination support.
     * 
     * @param pacienteId the ID of the patient
     * @param pageable pagination information
     * @return paginated list of clinical antecedents
     */
    @Query("SELECT ac FROM AntecedentesClinicos ac WHERE ac.paciente.id = :pacienteId")
    Page<AntecedentesClinicos> findByPacienteId(@Param("pacienteId") Integer pacienteId, Pageable pageable);
    
    /**
     * Finds clinical antecedents by condition type for nutritional planning.
     * Useful for identifying patients with specific conditions like diabetes, hypertension, etc.
     * 
     * @param condicionTipo the type of medical condition
     * @return list of patients with the specified condition
     */
    @Query("SELECT ac FROM AntecedentesClinicos ac WHERE LOWER(ac.condicion) LIKE LOWER(CONCAT('%', :condicionTipo, '%')) ORDER BY ac.paciente.id")
    List<AntecedentesClinicos> findByAntecedenteContainingIgnoreCase(@Param("antecedenteTipo") String antecedenteTipo);
    
    /**
     * Finds food allergies and intolerances for dietary planning.
     * Critical for creating safe nutrition plans.
     * 
     * @param pacienteId the ID of the patient
     * @return list of allergy-related antecedents
     */
    @Query("SELECT ac FROM AntecedentesClinicos ac WHERE ac.paciente.id = :pacienteId " +
           "AND (LOWER(ac.condicion) LIKE '%alergia%' OR LOWER(ac.condicion) LIKE '%intolerancia%' " +
           "OR LOWER(ac.condicion) LIKE '%celiac%' OR LOWER(ac.condicion) LIKE '%lactosa%')")
    List<AntecedentesClinicos> findAlergiasByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds cardiovascular conditions for specialized nutrition plans.
     * Important for heart-healthy diet recommendations.
     * 
     * @param pacienteId the ID of the patient
     * @return list of cardiovascular-related antecedents
     */
    @Query("SELECT ac FROM AntecedentesClinicos ac WHERE ac.paciente.id = :pacienteId " +
           "AND (LOWER(ac.condicion) LIKE '%cardio%' OR LOWER(ac.condicion) LIKE '%hipertens%' " +
           "OR LOWER(ac.condicion) LIKE '%corazón%' OR LOWER(ac.condicion) LIKE '%presión%')")
    List<AntecedentesClinicos> findCondicionesCardiovascularByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds diabetes-related conditions for glucose management nutrition plans.
     * Essential for diabetic diet planning.
     * 
     * @param pacienteId the ID of the patient
     * @return list of diabetes-related antecedents
     */
    @Query("SELECT ac FROM AntecedentesClinicos ac WHERE ac.paciente.id = :pacienteId " +
           "AND (LOWER(ac.condicion) LIKE '%diabetes%' OR LOWER(ac.condicion) LIKE '%glucos%' " +
           "OR LOWER(ac.condicion) LIKE '%insulin%' OR LOWER(ac.condicion) LIKE '%azúcar%')")
    List<AntecedentesClinicos> findCondicionesDiabeticasByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds gastrointestinal conditions affecting nutrition absorption.
     * Important for digestive health and nutrient planning.
     * 
     * @param pacienteId the ID of the patient
     * @return list of gastrointestinal-related antecedents
     */
    @Query("SELECT ac FROM AntecedentesClinicos ac WHERE ac.paciente.id = :pacienteId " +
           "AND (LOWER(ac.condicion) LIKE '%gastro%' OR LOWER(ac.condicion) LIKE '%intestin%' " +
           "OR LOWER(ac.condicion) LIKE '%digestiv%' OR LOWER(ac.condicion) LIKE '%estómago%')")
    List<AntecedentesClinicos> findCondicionesGastrointestinalByPacienteId(@Param("pacienteId") Integer pacienteId);
    
  
    /**
     * Counts total antecedents for a patient for summary statistics.
     * 
     * @param pacienteId the ID of the patient
     * @return total number of antecedents for the patient
     */
    @Query("SELECT COUNT(ac) FROM AntecedentesClinicos ac WHERE ac.paciente.id = :pacienteId")
    long countByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Checks if patient has any food allergies for safety verification.
     * 
     * @param pacienteId the ID of the patient
     * @return true if patient has any food allergies
     */
    @Query("SELECT CASE WHEN COUNT(ac) > 0 THEN TRUE ELSE FALSE END FROM AntecedentesClinicos ac " +
           "WHERE ac.paciente.id = :pacienteId " +
           "AND (LOWER(ac.condicion) LIKE '%alergia%' OR LOWER(ac.condicion) LIKE '%intolerancia%')")
    boolean hasAlergias(@Param("pacienteId") Integer pacienteId);
    
    // Spring Data JPA method naming conventions (automatically implemented)
    
    /**
     * Finds antecedents by patient using Spring Data method naming.
     * 
     * @param pacienteId the ID of the patient
     * @return list of antecedents ordered by ID
     */
    List<AntecedentesClinicos> findByPaciente_IdOrderByIdDesc(Integer pacienteId);
    
       /**    
     * Finds antecedents by description using Spring Data method naming.
     * 
     * @param descripcion the description to search for
     * @return list of matching antecedents
     */
    List<AntecedentesClinicos> findByDescripcionContainingIgnoreCase(String descripcion);
}
