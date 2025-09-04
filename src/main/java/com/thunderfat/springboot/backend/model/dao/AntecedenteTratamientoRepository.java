package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;

/**
 * Repository interface for managing AntecedenteTratamiento entities.
 * Provides CRUD operations and custom queries for treatment history management.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 2025
 */
@Repository
public interface AntecedenteTratamientoRepository extends BaseRepository<AntecedenteTratamiento, Integer> {
    
    /**
     * Finds all treatment antecedents for a specific patient.
     * 
     * @param pacienteId the ID of the patient
     * @return list of treatment antecedents for the patient
     */
    @Query("SELECT at FROM AntecedenteTratamiento at WHERE at.paciente.id = :pacienteId")
    List<AntecedenteTratamiento> findByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Alternative method using Spring Data JPA method naming convention.
     * This method is automatically implemented by Spring Data.
     * 
     * @param pacienteId the ID of the patient
     * @return list of treatment antecedents for the patient
     */
    List<AntecedenteTratamiento> findByPacienteIdOrderByFechaDesc(Integer pacienteId);
}
