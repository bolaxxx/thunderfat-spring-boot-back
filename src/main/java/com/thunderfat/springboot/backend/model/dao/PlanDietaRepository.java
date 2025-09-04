package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.PlanDieta;

/**
 * Repository interface for managing PlanDieta (Diet Plan) entities.
 * Follows Spring Boot 2025 best practices including:
 * - Pagination support for all list operations
 * - Caching for frequently accessed data
 * - EntityGraph for optimized loading
 * - Named parameters for better readability
 * - Business validation methods
 * - Date-based filtering and analytics
 * - Comprehensive diet plan management
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Repository
public interface PlanDietaRepository extends BaseRepository<PlanDieta, Integer> {
    
    // ================================
    // NUTRITIONIST-BASED QUERIES
    // ================================
    
    /**
     * Finds all diet plans created by a specific nutritionist with pagination.
     * Uses EntityGraph to optimize loading of related entities.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated list of diet plans
     */
    @EntityGraph(attributePaths = {"paciente", "nutricionista", "diasdieta"})
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId ORDER BY pd.fechaini DESC")
    Page<PlanDieta> findByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);
    
    /**
     * Finds diet plans by nutritionist (non-paginated for backward compatibility).
     * 
     * @param nutricionistaId the nutritionist ID
     * @return list of diet plans ordered by creation date (newest first)
     */
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId ORDER BY pd.fechaini DESC")
    List<PlanDieta> findByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Finds active diet plans for a nutritionist (plans that haven't ended yet).
     * 
     * @param nutricionistaId the nutritionist ID
     * @param currentDate the current date to compare against
     * @param pageable pagination information
     * @return paginated list of active diet plans
     */
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId " +
           "AND pd.fechafin >= :currentDate ORDER BY pd.fechaini DESC")
    Page<PlanDieta> findActiveByNutricionistaId(
        @Param("nutricionistaId") Integer nutricionistaId,
        @Param("currentDate") LocalDate currentDate,
        Pageable pageable
    );
    
    // ================================
    // PATIENT-BASED QUERIES
    // ================================
    
    /**
     * Finds all diet plans for a specific patient with pagination.
     * 
     * @param pacienteId the patient ID
     * @param pageable pagination information
     * @return paginated list of diet plans for the patient
     */
    @EntityGraph(attributePaths = {"diasdieta"})
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_paciente = :pacienteId ORDER BY pd.fechaini DESC")
    Page<PlanDieta> findByPacienteId(@Param("pacienteId") Integer pacienteId, Pageable pageable);
    
    /**
     * Finds diet plans by patient (non-paginated for backward compatibility).
     * 
     * @param pacienteId the patient ID
     * @return list of diet plans ordered by creation date (newest first)
     */
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_paciente = :pacienteId ORDER BY pd.fechaini DESC")
    List<PlanDieta> findByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds the current active diet plan for a patient.
     * This replaces the problematic native SQL query with proper JPQL.
     * 
     * @param pacienteId the patient ID
     * @param currentDate the current date to check against
     * @return optional containing the current active plan, if any
     */
    @Cacheable(value = "planesdieta", key = "'current_' + #pacienteId + '_' + #currentDate")
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_paciente = :pacienteId " +
           "AND pd.fechaini <= :currentDate AND pd.fechafin >= :currentDate " +
           "ORDER BY pd.fechaini DESC")
    Optional<PlanDieta> findCurrentActivePlanByPaciente(
        @Param("pacienteId") Integer pacienteId, 
        @Param("currentDate") LocalDate currentDate
    );
    
    /**
     * Finds the most recent diet plan for a patient (active or completed).
     * 
     * @param pacienteId the patient ID
     * @return optional containing the most recent plan
     */
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_paciente = :pacienteId " +
           "ORDER BY pd.fechaini DESC LIMIT 1")
    Optional<PlanDieta> findLatestByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Finds active diet plans for a patient (plans that haven't ended yet).
     * 
     * @param pacienteId the patient ID
     * @param currentDate the current date to compare against
     * @return list of active diet plans
     */
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_paciente = :pacienteId " +
           "AND pd.fechafin >= :currentDate ORDER BY pd.fechaini DESC")
    List<PlanDieta> findActiveByPacienteId(
        @Param("pacienteId") Integer pacienteId,
        @Param("currentDate") LocalDate currentDate
    );
    
    // ================================
    // DATE-BASED FILTERING
    // ================================
    
    /**
     * Finds diet plans within a specific date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return paginated list of diet plans in the date range
     */
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.fechaini BETWEEN :startDate AND :endDate " +
           "ORDER BY pd.fechaini DESC")
    Page<PlanDieta> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    
    /**
     * Finds diet plans that are active during a specific date.
     * 
     * @param date the date to check
     * @param pageable pagination information
     * @return paginated list of diet plans active on the given date
     */
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.fechaini <= :date AND pd.fechafin >= :date " +
           "ORDER BY pd.fechaini DESC")
    Page<PlanDieta> findActiveOnDate(@Param("date") LocalDate date, Pageable pageable);
    
    /**
     * Finds expired diet plans (plans that have ended before the current date).
     * 
     * @param currentDate the current date
     * @param pageable pagination information
     * @return paginated list of expired diet plans
     */
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.fechafin < :currentDate " +
           "ORDER BY pd.fechafin DESC")
    Page<PlanDieta> findExpiredPlans(@Param("currentDate") LocalDate currentDate, Pageable pageable);
    
    /**
     * Finds diet plans expiring within a specified number of days.
     * Useful for sending renewal reminders.
     * 
     * @param currentDate the current date
     * @param daysAhead number of days to look ahead
     * @param pageable pagination information
     * @return paginated list of plans expiring soon
     */
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.fechafin BETWEEN :currentDate AND :expirationDate " +
           "ORDER BY pd.fechafin ASC")
    Page<PlanDieta> findExpiringSoon(
        @Param("currentDate") LocalDate currentDate,
        @Param("expirationDate") LocalDate expirationDate,
        Pageable pageable
    );
    
    // ================================
    // BUSINESS VALIDATION METHODS
    // ================================
    
    /**
     * Checks if a patient has an active diet plan on a specific date.
     * 
     * @param pacienteId the patient ID
     * @param date the date to check
     * @return true if patient has an active plan on the date
     */
    @Query("SELECT CASE WHEN COUNT(pd) > 0 THEN true ELSE false END FROM PlanDieta pd " +
           "WHERE pd.id_paciente = :pacienteId AND pd.fechaini <= :date AND pd.fechafin >= :date")
    boolean hasActivePlanOnDate(@Param("pacienteId") Integer pacienteId, @Param("date") LocalDate date);
    
    /**
     * Checks if there are overlapping diet plans for a patient.
     * Useful for validation during plan creation.
     * 
     * @param pacienteId the patient ID
     * @param startDate the new plan start date
     * @param endDate the new plan end date
     * @param excludePlanId plan ID to exclude (for updates)
     * @return true if there are overlapping plans
     */
    @Query("SELECT CASE WHEN COUNT(pd) > 0 THEN true ELSE false END FROM PlanDieta pd " +
           "WHERE pd.id_paciente = :pacienteId " +
           "AND ((pd.fechaini BETWEEN :startDate AND :endDate) " +
           "OR (pd.fechafin BETWEEN :startDate AND :endDate) " +
           "OR (pd.fechaini <= :startDate AND pd.fechafin >= :endDate)) " +
           "AND (:excludePlanId IS NULL OR pd.id != :excludePlanId)")
    boolean hasOverlappingPlans(
        @Param("pacienteId") Integer pacienteId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("excludePlanId") Integer excludePlanId
    );
    
    // ================================
    // ANALYTICS AND REPORTING
    // ================================
    
    /**
     * Counts total diet plans created by a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return count of diet plans
     */
    @Query("SELECT COUNT(pd) FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId")
    Long countByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Counts active diet plans for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param currentDate the current date
     * @return count of active diet plans
     */
    @Query("SELECT COUNT(pd) FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId " +
           "AND pd.fechafin >= :currentDate")
    Long countActiveByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId, @Param("currentDate") LocalDate currentDate);
    
    /**
     * Counts expired diet plans for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param currentDate the current date
     * @return count of expired diet plans
     */
    @Query("SELECT COUNT(pd) FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId " +
           "AND pd.fechafin < :currentDate")
    Long countExpiredByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId, @Param("currentDate") LocalDate currentDate);
    
    /**
     * Counts diet plans expiring soon for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param currentDate the current date
     * @param endDate the end date for the range
     * @return count of plans expiring soon
     */
    @Query("SELECT COUNT(pd) FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId " +
           "AND pd.fechafin >= :currentDate AND pd.fechafin <= :endDate")
    Long countExpiringSoonByNutricionistaId(
        @Param("nutricionistaId") Integer nutricionistaId, 
        @Param("currentDate") LocalDate currentDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * Gets distinct patient IDs who have plans with a specific nutritionist.
     * Uses optimized query to avoid loading entire entities.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return set of distinct patient IDs
     */
    @Query("SELECT DISTINCT pd.id_paciente FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId")
    Set<Integer> findDistinctPatientIdsByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Gets distinct patient IDs who have active plans with a specific nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param currentDate the current date
     * @return set of distinct patient IDs with active plans
     */
    @Query("SELECT DISTINCT pd.id_paciente FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId " +
           "AND pd.fechafin >= :currentDate")
    Set<Integer> findDistinctActivePatientIdsByNutricionistaId(
        @Param("nutricionistaId") Integer nutricionistaId, 
        @Param("currentDate") LocalDate currentDate
    );
    
    /**
     * Calculates the average duration of diet plans for a nutritionist.
     * Uses DATEDIFF to calculate duration in days.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return average plan duration in days, or null if no plans
     */
    @Query("SELECT AVG(CAST(FUNCTION('DATEDIFF', pd.fechafin, pd.fechaini) AS DOUBLE)) FROM PlanDieta pd " +
           "WHERE pd.id_nutricionista = :nutricionistaId")
    Double calculateAveragePlanDuration(@Param("nutricionistaId") Integer nutricionistaId);

    /**
     * Counts total diet plans for a patient.
     * 
     * @param pacienteId the patient ID
     * @return count of diet plans for the patient
     */
    @Query("SELECT COUNT(pd) FROM PlanDieta pd WHERE pd.id_paciente = :pacienteId")
    Long countByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Gets average plan duration for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return average duration in days
     */
    @Query("SELECT AVG(DATEDIFF(pd.fechafin, pd.fechaini)) FROM PlanDieta pd " +
           "WHERE pd.id_nutricionista = :nutricionistaId")
    Double getAveragePlanDurationByNutritionist(@Param("nutricionistaId") Integer nutricionistaId);
    
    // ================================
    // CACHE MANAGEMENT
    // ================================
    
    /**
     * Evicts cache entries when a plan is saved.
     * 
     * @param planDieta the plan being saved
     * @return the saved plan
     */
    @CacheEvict(value = "planesdieta", allEntries = true)
    @Override
    @NonNull
    <S extends PlanDieta> S save(@NonNull S planDieta);
    
    /**
     * Evicts cache entries when a plan is deleted.
     * 
     * @param id the plan ID to delete
     */
    @CacheEvict(value = "planesdieta", allEntries = true)
    @Override
    void deleteById(@NonNull Integer id);
    
    // ================================
    // BULK OPERATIONS
    // ================================
    
    /**
     * Bulk update to extend end dates of active plans.
     * Uses native MySQL DATE_ADD function for date manipulation.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param daysToExtend number of days to extend
     * @param currentDate the current date
     * @return number of updated plans
     */
    @Modifying
    @CacheEvict(value = "planesdieta", allEntries = true)
    @Query(value = "UPDATE plan_dieta SET fechafin = DATE_ADD(fechafin, INTERVAL ?2 DAY) " +
           "WHERE id_nutricionista = ?1 AND fechafin >= ?3", nativeQuery = true)
    int extendActivePlans(
        @Param("nutricionistaId") Integer nutricionistaId,
        @Param("daysToExtend") int daysToExtend,
        @Param("currentDate") LocalDate currentDate
    );
    
    // ================================
    // SPRING DATA JPA METHOD NAMING CONVENTIONS
    // ================================
    
    /**
     * Spring Data JPA method naming convention examples.
     * These methods are automatically implemented by Spring Data.
     */
    
    // Find by date range using method naming
    List<PlanDieta> findByFechainiBetweenOrderByFechainiDesc(LocalDate startDate, LocalDate endDate);
    
    // Find by patient and date range - using explicit query to avoid field name confusion
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_paciente = :pacienteId " +
           "AND pd.fechaini BETWEEN :startDate AND :endDate ORDER BY pd.fechaini DESC")
    List<PlanDieta> findByIdPacienteAndFechainiBetweenOrderByFechainiDesc(
        @Param("pacienteId") Integer pacienteId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    // Find by nutritionist and date range - using explicit query to avoid field name confusion
    @Query("SELECT pd FROM PlanDieta pd WHERE pd.id_nutricionista = :nutricionistaId " +
           "AND pd.fechaini BETWEEN :startDate AND :endDate ORDER BY pd.fechaini DESC")
    List<PlanDieta> findByIdNutricionistaAndFechainiBetweenOrderByFechainiDesc(
        @Param("nutricionistaId") Integer nutricionistaId, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
    
    // Check existence by patient and date overlap
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PlanDieta p WHERE p.id_paciente = ?1 AND p.fechaini <= ?2 AND p.fechafin >= ?3")
    boolean existsByPacienteAndDateOverlap(
        Integer pacienteId, LocalDate endDate, LocalDate startDate
    );
}
