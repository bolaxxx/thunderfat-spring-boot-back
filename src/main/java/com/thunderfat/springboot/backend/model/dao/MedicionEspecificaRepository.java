package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;

/**
 * Repository interface for managing MedicionEspecifica entities.
 * Handles specific body measurements (waist, hip, arm circumference, etc.) for patient tracking.
 * Provides CRUD operations and specialized queries for nutrition progress monitoring.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 2025
 */
@Repository
public interface MedicionEspecificaRepository extends JpaRepository<MedicionEspecifica, Integer> {
    
    /**
     * Finds all specific measurements for a patient, ordered by measurement date (newest first).
     * 
     * @param pacienteId the ID of the patient
     * @return list of specific measurements for the patient
     */
    @Query("SELECT me FROM MedicionEspecifica me WHERE me.id_paciente = :pacienteId ORDER BY me.fecha DESC")
    @RestResource(path = "findByPacienteIdList", rel = "findByPacienteIdList")
    List<MedicionEspecifica> findByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds specific measurements for a patient with pagination support.
     * 
     * @param pacienteId the ID of the patient
     * @param pageable pagination information
     * @return paginated list of specific measurements
     */
    @Query("SELECT me FROM MedicionEspecifica me WHERE me.id_paciente = :pacienteId")
    @RestResource(path = "findByPacienteIdPaged", rel = "findByPacienteIdPaged")
    Page<MedicionEspecifica> findByPacienteId(@Param("pacienteId") Integer pacienteId, Pageable pageable);
    
    /**
     * Finds the most recent specific measurement for a patient.
     * 
     * @param pacienteId the ID of the patient
     * @return the most recent measurement, if any
     */
    @Query("SELECT me FROM MedicionEspecifica me WHERE me.id_paciente = :pacienteId ORDER BY me.fecha DESC LIMIT 1")
    Optional<MedicionEspecifica> findLatestByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds specific measurements for a patient within a date range.
     * 
     * @param pacienteId the ID of the patient
     * @param fechaInicio start date (inclusive)
     * @param fechaFin end date (inclusive)
     * @return list of measurements within the date range
     */
    @Query("SELECT me FROM MedicionEspecifica me WHERE me.id_paciente = :pacienteId " +
           "AND me.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY me.fecha DESC")
    List<MedicionEspecifica> findByPacienteIdAndFechaBetween(
        @Param("pacienteId") Integer pacienteId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );
    
    /**
     * Alternative method using Spring Data JPA method naming convention.
     * Uses explicit query to avoid field name confusion since entity uses id_paciente.
     * 
     * @param pacienteId the ID of the patient
     * @return list of specific measurements ordered by date (descending)
     */
    @Query("SELECT me FROM MedicionEspecifica me WHERE me.id_paciente = :pacienteId ORDER BY me.fecha DESC")
    List<MedicionEspecifica> findByPacienteIdOrderByFechaDesc(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Spring Data JPA method to find measurements within date range.
     * Uses explicit query to avoid field name confusion since entity uses id_paciente.
     * 
     * @param pacienteId the ID of the patient
     * @param fechaInicio start date
     * @param fechaFin end date
     * @return list of measurements within the range
     */
    @Query("SELECT me FROM MedicionEspecifica me WHERE me.id_paciente = :pacienteId " +
           "AND me.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY me.fecha DESC")
    List<MedicionEspecifica> findByPacienteIdAndFechaBetweenOrderByFechaDesc(
        @Param("pacienteId") Integer pacienteId, 
        @Param("fechaInicio") LocalDate fechaInicio, 
        @Param("fechaFin") LocalDate fechaFin
    );
}
