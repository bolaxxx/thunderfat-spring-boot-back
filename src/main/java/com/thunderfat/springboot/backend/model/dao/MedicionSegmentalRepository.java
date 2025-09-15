package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;

/**
 * Repository interface for managing MedicionSegmental (Segmental Measurement) entities.
 * Follows Spring Boot 2025 best practices including:
 * - BaseRepository inheritance for consistency
 * - Named parameters for better readability
 * - Pagination support for all list operations
 * - Caching for frequently accessed data
 * - Comprehensive measurement tracking queries
 * - Date-based filtering and analytics
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Repository
public interface MedicionSegmentalRepository extends BaseRepository<MedicionSegmental, Integer> {
    
    // ================================
    // PATIENT-BASED QUERIES
    // ================================
    
    /**
     * Finds all segmental measurements for a specific patient with pagination.
     * 
     * @param pacienteId the patient ID
     * @param pageable pagination information
     * @return paginated list of segmental measurements
     */
    @Cacheable(value = "mediciones-segmentales", key = "'patient_' + #pacienteId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId ORDER BY ms.fecha DESC")
    @RestResource(path = "findByPacienteIdPaged", rel = "findByPacienteIdPaged")
    Page<MedicionSegmental> findByPacienteId(@Param("pacienteId") Integer pacienteId, Pageable pageable);
    
    /**
     * Finds all segmental measurements for a patient (non-paginated for backward compatibility).
     * 
     * @param pacienteId the patient ID
     * @return list of segmental measurements ordered by date (newest first)
     */
    @Cacheable(value = "mediciones-segmentales", key = "'patient_all_' + #pacienteId")
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId ORDER BY ms.fecha DESC")
    @RestResource(path = "findByPacienteIdList", rel = "findByPacienteIdList")
    List<MedicionSegmental> findByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds segmental measurements for a patient within a specific date range.
     * 
     * @param pacienteId the patient ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return paginated list of measurements in the date range
     */
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId " +
           "AND ms.fecha BETWEEN :startDate AND :endDate ORDER BY ms.fecha DESC")
    Page<MedicionSegmental> findByPacienteIdAndDateRange(
        @Param("pacienteId") Integer pacienteId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    
    /**
     * Finds the most recent segmental measurement for a patient.
     * 
     * @param pacienteId the patient ID
     * @return optional containing the most recent measurement
     */
    @Cacheable(value = "mediciones-segmentales", key = "'latest_' + #pacienteId")
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId " +
           "ORDER BY ms.fecha DESC LIMIT 1")
    Optional<MedicionSegmental> findLatestByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds segmental measurements for a patient on a specific date.
     * 
     * @param pacienteId the patient ID
     * @param fecha the measurement date
     * @return list of measurements on the specified date
     */
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId " +
           "AND ms.fecha = :fecha ORDER BY ms.id DESC")
    List<MedicionSegmental> findByPacienteIdAndFecha(
        @Param("pacienteId") Integer pacienteId,
        @Param("fecha") LocalDate fecha
    );
    
    // ================================
    // DATE-BASED QUERIES
    // ================================
    
    /**
     * Finds all segmental measurements within a date range across all patients.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return paginated list of measurements in the date range
     */
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.fecha BETWEEN :startDate AND :endDate " +
           "ORDER BY ms.fecha DESC")
    Page<MedicionSegmental> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    
    /**
     * Finds segmental measurements taken on a specific date.
     * 
     * @param fecha the measurement date
     * @param pageable pagination information
     * @return paginated list of measurements on the specified date
     */
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.fecha = :fecha ORDER BY ms.id DESC")
    Page<MedicionSegmental> findByFecha(@Param("fecha") LocalDate fecha, Pageable pageable);
    
    /**
     * Finds recent segmental measurements (within last N days).
     * 
     * @param daysBack number of days to look back
     * @param pageable pagination information
     * @return paginated list of recent measurements
     */
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.fecha >= :cutoffDate ORDER BY ms.fecha DESC")
    Page<MedicionSegmental> findRecentMeasurements(
        @Param("cutoffDate") LocalDate cutoffDate,
        Pageable pageable
    );
    
    // ================================
    // ANALYTICS AND REPORTING
    // ================================
    
    /**
     * Counts total segmental measurements for a patient.
     * 
     * @param pacienteId the patient ID
     * @return count of measurements
     */
    @Query("SELECT COUNT(ms) FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId")
    Long countByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Counts segmental measurements for a patient within a date range.
     * 
     * @param pacienteId the patient ID
     * @param startDate the start date
     * @param endDate the end date
     * @return count of measurements in the date range
     */
    @Query("SELECT COUNT(ms) FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId " +
           "AND ms.fecha BETWEEN :startDate AND :endDate")
    Long countByPacienteIdAndDateRange(
        @Param("pacienteId") Integer pacienteId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Gets average body fat percentage for a patient over a date range.
     * 
     * @param pacienteId the patient ID
     * @param startDate the start date
     * @param endDate the end date
     * @return average total body fat percentage
     */
    @Query("SELECT AVG(ms.tporcentajegrasa) FROM MedicionSegmental ms " +
           "WHERE ms.id_paciente = :pacienteId AND ms.fecha BETWEEN :startDate AND :endDate")
    Double getAverageBodyFatPercentage(
        @Param("pacienteId") Integer pacienteId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Gets average muscle mass for a patient over a date range.
     * 
     * @param pacienteId the patient ID
     * @param startDate the start date
     * @param endDate the end date
     * @return average total muscle mass
     */
    @Query("SELECT AVG(ms.tmusculo) FROM MedicionSegmental ms " +
           "WHERE ms.id_paciente = :pacienteId AND ms.fecha BETWEEN :startDate AND :endDate")
    Double getAverageMuscleMass(
        @Param("pacienteId") Integer pacienteId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Finds the measurement with the highest muscle mass for a patient.
     * 
     * @param pacienteId the patient ID
     * @return optional containing the measurement with highest muscle mass
     */
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId " +
           "ORDER BY ms.tmusculo DESC LIMIT 1")
    Optional<MedicionSegmental> findMaxMuscleMassByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds the measurement with the lowest body fat percentage for a patient.
     * 
     * @param pacienteId the patient ID
     * @return optional containing the measurement with lowest body fat percentage
     */
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId " +
           "ORDER BY ms.tporcentajegrasa ASC LIMIT 1")
    Optional<MedicionSegmental> findMinBodyFatByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    // ================================
    // BODY SEGMENT SPECIFIC QUERIES
    // ================================
    
    /**
     * Finds measurements with specific body segment criteria.
     * For example, finding patients with high arm muscle mass.
     * 
     * @param minArmMuscle minimum arm muscle mass
     * @param pacienteId the patient ID (optional filter)
     * @param pageable pagination information
     * @return paginated list of measurements meeting the criteria
     */
    @Query("SELECT ms FROM MedicionSegmental ms WHERE " +
           "(:pacienteId IS NULL OR ms.id_paciente = :pacienteId) " +
           "AND (ms.bdmusculo + ms.bimusculo) >= :minArmMuscle " +
           "ORDER BY ms.fecha DESC")
    Page<MedicionSegmental> findByMinArmMuscle(
        @Param("minArmMuscle") Double minArmMuscle,
        @Param("pacienteId") Integer pacienteId,
        Pageable pageable
    );
    
    /**
     * Finds measurements with balanced muscle distribution (similar muscle mass across segments).
     * 
     * @param pacienteId the patient ID
     * @param tolerance the tolerance for muscle mass variation
     * @param pageable pagination information
     * @return paginated list of balanced measurements
     */
    @Query("SELECT ms FROM MedicionSegmental ms WHERE ms.id_paciente = :pacienteId " +
           "AND ABS(ms.bdmusculo - ms.bimusculo) <= :tolerance " +
           "AND ABS(ms.pdmusculo - ms.pimusculo) <= :tolerance " +
           "ORDER BY ms.fecha DESC")
    Page<MedicionSegmental> findBalancedMuscleDistribution(
        @Param("pacienteId") Integer pacienteId,
        @Param("tolerance") Double tolerance,
        Pageable pageable
    );
    
    // ================================
    // PROGRESS TRACKING QUERIES
    // ================================
    
    /**
     * Gets muscle mass progress between two dates for a patient.
     * 
     * @param pacienteId the patient ID
     * @param startDate the comparison start date
     * @param endDate the comparison end date
     * @return muscle mass difference (positive = gain, negative = loss)
     */
    @Query("SELECT " +
           "(SELECT AVG(ms2.tmusculo) FROM MedicionSegmental ms2 " +
           " WHERE ms2.id_paciente = :pacienteId AND ms2.fecha >= :endDate) - " +
           "(SELECT AVG(ms1.tmusculo) FROM MedicionSegmental ms1 " +
           " WHERE ms1.id_paciente = :pacienteId AND ms1.fecha <= :startDate)")
    Double getMuscleMassProgress(
        @Param("pacienteId") Integer pacienteId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Gets body fat percentage progress between two dates for a patient.
     * 
     * @param pacienteId the patient ID
     * @param startDate the comparison start date
     * @param endDate the comparison end date
     * @return body fat percentage difference (negative = loss, positive = gain)
     */
    @Query("SELECT " +
           "(SELECT AVG(ms2.tporcentajegrasa) FROM MedicionSegmental ms2 " +
           " WHERE ms2.id_paciente = :pacienteId AND ms2.fecha >= :endDate) - " +
           "(SELECT AVG(ms1.tporcentajegrasa) FROM MedicionSegmental ms1 " +
           " WHERE ms1.id_paciente = :pacienteId AND ms1.fecha <= :startDate)")
    Double getBodyFatProgress(
        @Param("pacienteId") Integer pacienteId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // ================================
    // LEGACY SUPPORT (DEPRECATED)
    // ================================
    
    /**
     * @deprecated Use {@link #findByPacienteId(Integer)} instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    default List<MedicionSegmental> buscarporPaciente(int id_paciente) {
        return findByPacienteId(id_paciente);
    }
}
