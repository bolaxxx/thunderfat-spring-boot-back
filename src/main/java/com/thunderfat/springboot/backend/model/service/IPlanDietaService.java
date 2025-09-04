package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.model.dto.NutricionistaStatsDTO;
import com.thunderfat.springboot.backend.model.dto.PlanDietaDTO;
import com.thunderfat.springboot.backend.model.entity.Ingrediente;

/**
 * Service interface for managing PlanDieta (Diet Plan) operations.
 * Follows Spring Boot 2025 best practices including:
 * - DTO pattern for data transfer
 * - Pagination support for list operations
 * - Optional return types for null safety
 * - Business validation methods
 * - Comprehensive CRUD operations
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
public interface IPlanDietaService {
    
    // ================================
    // CORE CRUD OPERATIONS
    // ================================
    
    /**
     * Creates a new diet plan with validation.
     * Validates that there are no overlapping plans for the patient.
     * 
     * @param planDietaDTO the diet plan data
     * @param nutricionistaId the nutritionist ID
     * @param pacienteId the patient ID
     * @return the created diet plan
     * @throws BusinessException if validation fails
     */
    PlanDietaDTO createPlan(PlanDietaDTO planDietaDTO, Integer nutricionistaId, Integer pacienteId);
    
    /**
     * Updates an existing diet plan with validation.
     * 
     * @param id the plan ID to update
     * @param planDietaDTO the updated plan data
     * @param nutricionistaId the nutritionist ID
     * @param pacienteId the patient ID
     * @return the updated diet plan
     * @throws BusinessException if validation fails
     */
    PlanDietaDTO updatePlan(Integer id, PlanDietaDTO planDietaDTO, Integer nutricionistaId, Integer pacienteId);
    
    /**
     * Finds a diet plan by ID.
     * 
     * @param id the plan ID
     * @return optional containing the diet plan if found
     */
    Optional<PlanDietaDTO> findById(Integer id);
    
    /**
     * Deletes a diet plan by ID.
     * Performs validation to ensure the plan can be safely deleted.
     * 
     * @param id the plan ID to delete
     * @throws BusinessException if the plan cannot be deleted
     */
    void deleteById(Integer id);
    
    // ================================
    // NUTRITIONIST-BASED OPERATIONS
    // ================================
    
    /**
     * Finds all diet plans for a nutritionist with pagination.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated list of diet plans
     */
    Page<PlanDietaDTO> findByNutricionistaId(Integer nutricionistaId, Pageable pageable);
    
    /**
     * Finds all diet plans for a nutritionist (non-paginated).
     * 
     * @param nutricionistaId the nutritionist ID
     * @return list of diet plans
     */
    List<PlanDietaDTO> findByNutricionistaId(Integer nutricionistaId);
    
    /**
     * Finds active diet plans for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated list of active diet plans
     */
    Page<PlanDietaDTO> findActiveByNutricionistaId(Integer nutricionistaId, Pageable pageable);
    
    /**
     * Gets statistics for a nutritionist's diet plans.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return statistics object
     */
    NutricionistaStatsDTO getNutricionistaStatistics(Integer nutricionistaId);
    
    // ================================
    // PATIENT-BASED OPERATIONS
    // ================================
    
    /**
     * Finds all diet plans for a patient with pagination.
     * 
     * @param pacienteId the patient ID
     * @param pageable pagination information
     * @return paginated list of diet plans
     */
    Page<PlanDietaDTO> findByPacienteId(Integer pacienteId, Pageable pageable);
    
    /**
     * Finds all diet plans for a patient (non-paginated).
     * 
     * @param pacienteId the patient ID
     * @return list of diet plans
     */
    List<PlanDietaDTO> findByPacienteId(Integer pacienteId);
    
    /**
     * Finds the current active diet plan for a patient.
     * Replaces the old buscarPlanActualPaciente method.
     * 
     * @param pacienteId the patient ID
     * @param currentDate the current date (defaults to today if null)
     * @return optional containing the current active plan
     */
    Optional<PlanDietaDTO> findCurrentActivePlan(Integer pacienteId, LocalDate currentDate);
    
    /**
     * Finds the current active diet plan for a patient (uses today's date).
     * 
     * @param pacienteId the patient ID
     * @return optional containing the current active plan
     */
    Optional<PlanDietaDTO> findCurrentActivePlan(Integer pacienteId);
    
    /**
     * Finds the most recent diet plan for a patient.
     * 
     * @param pacienteId the patient ID
     * @return optional containing the most recent plan
     */
    Optional<PlanDietaDTO> findLatestByPacienteId(Integer pacienteId);
    
    // ================================
    // DATE-BASED OPERATIONS
    // ================================
    
    /**
     * Finds diet plans within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable pagination information
     * @return paginated list of diet plans in the date range
     */
    Page<PlanDietaDTO> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * Finds diet plans expiring soon (for renewal reminders).
     * 
     * @param daysAhead number of days to look ahead
     * @param pageable pagination information
     * @return paginated list of expiring plans
     */
    Page<PlanDietaDTO> findExpiringSoon(Integer daysAhead, Pageable pageable);
    
    /**
     * Finds expired diet plans.
     * 
     * @param pageable pagination information
     * @return paginated list of expired plans
     */
    Page<PlanDietaDTO> findExpiredPlans(Pageable pageable);
    
    // ================================
    // BUSINESS LOGIC OPERATIONS
    // ================================
    
    /**
     * Validates if a new diet plan can be created without conflicts.
     * 
     * @param pacienteId the patient ID
     * @param startDate the plan start date
     * @param endDate the plan end date
     * @param excludePlanId plan ID to exclude (for updates)
     * @return true if the plan can be created
     */
    boolean validatePlanDates(Integer pacienteId, LocalDate startDate, LocalDate endDate, Integer excludePlanId);
    
    /**
     * Extends active diet plans for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param daysToExtend number of days to extend
     * @return number of plans extended
     */
    int extendActivePlans(Integer nutricionistaId, Integer daysToExtend);
    
    /**
     * Generates a shopping list for a patient's current diet plan.
     * Replaces the old listadelacompra method.
     * 
     * @param pacienteId the patient ID
     * @param date the date for the shopping list (defaults to today if null)
     * @return list of ingredients needed
     */
    List<Ingrediente> generateShoppingList(Integer pacienteId, LocalDate date);
    
    /**
     * Generates a shopping list for a patient's current diet plan (uses today's date).
     * 
     * @param pacienteId the patient ID
     * @return list of ingredients needed
     */
    List<Ingrediente> generateShoppingList(Integer pacienteId);
    
    // ================================
    // LEGACY SUPPORT (DEPRECATED)
    // ================================
    
    /**
     * @deprecated Use {@link #findById(Integer)} instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    default Optional<PlanDietaDTO> buscarPorId(int id_plandieta) {
        return findById(id_plandieta);
    }
    
    /**
     * @deprecated Use {@link #findCurrentActivePlan(Integer, LocalDate)} instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    default Optional<PlanDietaDTO> buscarPlanActualPaciente(int paciente, LocalDate fecha_actual) {
        return findCurrentActivePlan(paciente, fecha_actual);
    }
    
    /**
     * @deprecated Use {@link #createPlan(PlanDietaDTO, Integer, Integer)} instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    default PlanDietaDTO insertar(PlanDietaDTO planDieta, int id_nutricionista, int id_paciente) {
        return createPlan(planDieta, id_nutricionista, id_paciente);
    }
    
    /**
     * @deprecated Use {@link #findByNutricionistaId(Integer)} instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    default List<PlanDietaDTO> listarPorNutricionista(int id) {
        return findByNutricionistaId(id);
    }
    
    /**
     * @deprecated Use {@link #generateShoppingList(Integer, LocalDate)} instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    default List<Ingrediente> listadelacompra(int id_paciente, LocalDate fecha_actual) {
        return generateShoppingList(id_paciente, fecha_actual);
    }
    
    /**
     * @deprecated Use {@link #deleteById(Integer)} instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    default void eliminar(int id_plandieta) {
        deleteById(id_plandieta);
    }
    
    /**
     * @deprecated Use {@link #updatePlan(Integer, PlanDietaDTO, Integer, Integer)} instead
     */
    @Deprecated(since = "3.5.4", forRemoval = true)
    default PlanDietaDTO updatePlan(PlanDietaDTO dieta, int id_nutricionista, int id_paciente) {
        // This method signature is problematic as it lacks the plan ID
        throw new UnsupportedOperationException("Use updatePlan(Integer id, PlanDietaDTO, Integer, Integer) instead");
    }
}
