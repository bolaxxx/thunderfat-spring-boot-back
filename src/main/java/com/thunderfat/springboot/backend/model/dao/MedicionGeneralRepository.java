package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;

/**
 * Repository interface for managing MedicionGeneral entities.
 * Handles general body measurements (weight, height, BMI, etc.) for comprehensive patient tracking.
 * Provides CRUD operations and specialized queries for nutrition progress monitoring and analysis.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 2025
 */
@Repository
public interface MedicionGeneralRepository extends JpaRepository<MedicionGeneral, Integer> {
    
    /**
     * Finds all general measurements for a patient, ordered by measurement date (newest first).
     * 
     * @param pacienteId the ID of the patient
     * @return list of general measurements for the patient
     */
    @Query("SELECT mg FROM MedicionGeneral mg WHERE mg.id_paciente = :pacienteId ORDER BY mg.fecha DESC")
    List<MedicionGeneral> findByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds general measurements for a patient with pagination support.
     * 
     * @param pacienteId the ID of the patient
     * @param pageable pagination information
     * @return paginated list of general measurements
     */
    @Query("SELECT mg FROM MedicionGeneral mg WHERE mg.id_paciente = :pacienteId")
    Page<MedicionGeneral> findByPacienteId(@Param("pacienteId") Integer pacienteId, Pageable pageable);
    
    /**
     * Finds the most recent general measurement for a patient.
     * 
     * @param pacienteId the ID of the patient
     * @return the most recent measurement, if any
     */
    @Query("SELECT mg FROM MedicionGeneral mg WHERE mg.id_paciente = :pacienteId ORDER BY mg.fecha DESC LIMIT 1")
    Optional<MedicionGeneral> findLatestByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Gets the most recent weight value for a patient.
     * Optimized query for quick weight retrieval in nutrition tracking.
     * 
     * @param pacienteId the ID of the patient
     * @return the most recent weight value, or null if no measurements exist
     */
    @Query(value = "SELECT peso_actual FROM medicion_general " +
                   "WHERE id_paciente = :pacienteId " +
                   "ORDER BY fecha DESC LIMIT 1", nativeQuery = true)
    Double findLatestWeightByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Gets weight history for trend analysis.
     * Returns weight values with dates for charting and progress tracking.
     * 
     * @param pacienteId the ID of the patient
     * @return list of weight measurements ordered by date
     */
    @Query("SELECT NEW map(mg.fecha as fecha, mg.pesoactual as peso) " +
           "FROM MedicionGeneral mg WHERE mg.id_paciente = :pacienteId " +
           "ORDER BY mg.fecha ASC")
    List<Object> findWeightHistoryByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds general measurements for a patient within a date range.
     * 
     * @param pacienteId the ID of the patient
     * @param fechaInicio start date (inclusive)
     * @param fechaFin end date (inclusive)
     * @return list of measurements within the date range
     */
    @Query("SELECT mg FROM MedicionGeneral mg WHERE mg.id_paciente = :pacienteId " +
           "AND mg.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY mg.fecha DESC")
    List<MedicionGeneral> findByPacienteIdAndFechaBetween(
        @Param("pacienteId") Integer pacienteId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );
    
    /**
     * Calculates BMI trend for a patient over time.
     * Returns BMI values with dates for progress analysis.
     * 
     * @param pacienteId the ID of the patient
     * @return list of BMI measurements ordered by date
     */
    @Query("SELECT NEW map(mg.fecha as fecha, mg.imc as bmi) " +
           "FROM MedicionGeneral mg WHERE mg.id_paciente = :pacienteId " +
           "AND mg.imc IS NOT NULL ORDER BY mg.fecha ASC")
    List<Object> findBmiHistoryByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    // Spring Data JPA method naming conventions (automatically implemented)
    
    /**
     * Finds measurements ordered by date using Spring Data method naming.
     * 
     * @param id_paciente the ID of the patient
     * @return list of general measurements ordered by date (descending)
     */
    @Query("SELECT mg FROM MedicionGeneral mg WHERE mg.id_paciente = :id_paciente ORDER BY mg.fecha DESC")
    List<MedicionGeneral> findByIdPacienteOrderByFechaDesc(@Param("id_paciente") Integer id_paciente);
    
    /**
     * Finds measurements within date range using Spring Data method naming.
     * 
     * @param id_paciente the ID of the patient
     * @param fechaInicio start date
     * @param fechaFin end date
     * @return list of measurements within the range
     */
    @Query("SELECT mg FROM MedicionGeneral mg WHERE mg.id_paciente = :id_paciente " +
           "AND mg.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY mg.fecha DESC")
    List<MedicionGeneral> findByIdPacienteAndFechaBetweenOrderByFechaDesc(
        @Param("id_paciente") Integer id_paciente, 
        @Param("fechaInicio") LocalDate fechaInicio, 
        @Param("fechaFin") LocalDate fechaFin
    );
    
    /**
     * Finds the first measurement for a patient (baseline).
     * 
     * @param id_paciente the ID of the patient
     * @return the earliest measurement for trend analysis
     */
    @Query("SELECT mg FROM MedicionGeneral mg WHERE mg.id_paciente = :id_paciente ORDER BY mg.fecha ASC LIMIT 1")
    Optional<MedicionGeneral> findFirstByIdPacienteOrderByFechaAsc(@Param("id_paciente") Integer id_paciente);
}
