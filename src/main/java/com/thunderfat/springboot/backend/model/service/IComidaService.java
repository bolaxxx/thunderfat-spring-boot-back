package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.model.dto.ComidaDTO;
import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;

/**
 * Modern Spring Boot 2025 service interface for Comida (Meal) operations.
 * Provides comprehensive meal management functionality for nutrition plans.
 * 
 * Features:
 * - Legacy method support for backward compatibility
 * - Modern DTO-based operations with pagination
 * - Meal analytics and nutritional analysis
 * - Meal substitution recommendations
 * - Performance optimized with caching
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
public interface IComidaService {
    
    // ================================
    // LEGACY CRUD OPERATIONS (DEPRECATED)
    // ================================
    
    /**
     * @deprecated Use save(ComidaDTO) instead for better DTO pattern
     */
    @Deprecated
    void insertar(com.thunderfat.springboot.backend.model.entity.Comida comida);
    
    /**
     * @deprecated Use deleteById(Integer) instead for consistent parameter types
     */
    @Deprecated
    void eliminar(int id_comida);
    
    /**
     * @deprecated Use findByPlanDietaId(Integer, Pageable) instead for pagination
     */
    @Deprecated
    List<com.thunderfat.springboot.backend.model.entity.Comida> listaPorPlanDieta(com.thunderfat.springboot.backend.model.entity.PlanDieta planDieta);
    
    /**
     * @deprecated Use findById(Integer) instead for Optional return
     */
    @Deprecated
    com.thunderfat.springboot.backend.model.entity.Comida buscarPorID(int id_comida);
    
    /**
     * @deprecated Use findMealSubstitutions(Integer, Integer) instead for DTO pattern
     */
    @Deprecated
    List<com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado> bucarcambios(int id_paciente, int id_plato);
    
    // ================================
    // MODERN DTO-BASED OPERATIONS
    // ================================
    
    /**
     * Get all meals with pagination
     * @param pageable Pagination information
     * @return Page of meal DTOs
     */
    Page<ComidaDTO> findAll(Pageable pageable);
    
    /**
     * Find meal by ID
     * @param id Meal ID
     * @return Optional meal DTO
     */
    Optional<ComidaDTO> findById(Integer id);
    
    /**
     * Save or update a meal
     * @param comidaDTO Meal data
     * @return Saved meal DTO
     */
    ComidaDTO save(ComidaDTO comidaDTO);
    
    /**
     * Delete meal by ID
     * @param id Meal ID
     */
    void deleteById(Integer id);
    
    // ================================
    // BUSINESS-SPECIFIC OPERATIONS
    // ================================
    
    /**
     * Find meals by diet plan ID with pagination
     * @param planDietaId Diet plan ID
     * @param pageable Pagination information
     * @return Page of meal DTOs
     */
    Page<ComidaDTO> findByPlanDietaId(Integer planDietaId, Pageable pageable);
    
    /**
     * Find meals by nutritionist ID
     * @param nutricionistaId Nutritionist ID
     * @param pageable Pagination information
     * @return Page of meal DTOs
     */
    Page<ComidaDTO> findByNutricionistaId(Integer nutricionistaId, Pageable pageable);
    
    /**
     * Find today's meals for a patient
     * @param pacienteId Patient ID
     * @param date Target date
     * @return List of meal DTOs
     */
    List<ComidaDTO> findTodayMeals(Integer pacienteId, LocalDate date);
    
    /**
     * Find meal substitutions for a patient
     * @param pacienteId Patient ID
     * @param platoId Current plate ID
     * @return List of possible meal substitutions
     */
    List<PlatoPredeterminadoDTO> findMealSubstitutions(Integer pacienteId, Integer platoId);
    
    // ================================
    // ANALYTICS & REPORTING
    // ================================
    
    /**
     * Count total meals by nutritionist
     * @param nutricionistaId Nutritionist ID
     * @return Total meal count
     */
    Long countMealsByNutritionist(Integer nutricionistaId);
    
    /**
     * Count active meals by nutritionist
     * @param nutricionistaId Nutritionist ID
     * @return Active meal count
     */
    Long countActiveMealsByNutritionist(Integer nutricionistaId);
    
    /**
     * Calculate average meal calories by nutritionist
     * @param nutricionistaId Nutritionist ID
     * @return Average calories
     */
    Double getAverageMealCalories(Integer nutricionistaId);
    
    /**
     * Find most popular meals by nutritionist
     * @param nutricionistaId Nutritionist ID
     * @param pageable Pagination information
     * @return Page of popular meal DTOs
     */
    Page<ComidaDTO> findMostPopularMeals(Integer nutricionistaId, Pageable pageable);
    
    /**
     * Get meal statistics for a nutritionist
     * @param nutricionistaId Nutritionist ID
     * @return Meal statistics data
     */
    // ComidaStatsDTO getMealStatistics(Integer nutricionistaId); // TODO: Create stats DTO
    
    /**
     * Find meals by time range for a patient
     * @param pacienteId Patient ID
     * @param horaInicio Start time
     * @param horaFin End time
     * @return List of meal DTOs within the specified time range
     */
    List<ComidaDTO> findByTimeRange(Integer pacienteId, LocalTime horaInicio, LocalTime horaFin);
    
    /**
     * Find highly rated meals by nutritionist (rating >= 4)
     * @param nutricionistaId Nutritionist ID
     * @param pageable Pagination information
     * @return Page of highly rated meal DTOs
     */
    Page<ComidaDTO> findHighRatedMealsByNutritionist(Integer nutricionistaId, Pageable pageable);
    
    /**
     * Find low rated meals that need attention (rating <= 2)
     * @param nutricionistaId Nutritionist ID
     * @return List of low rated meal DTOs
     */
    List<ComidaDTO> findLowRatedMealsByNutritionist(Integer nutricionistaId);
    
    /**
     * Find meals by date range for a nutritionist
     * @param nutricionistaId Nutritionist ID
     * @param fechaInicio Start date
     * @param fechaFin End date
     * @param pageable Pagination information
     * @return Page of meal DTOs within the specified date range
     */
    Page<ComidaDTO> findByDateRangeAndNutritionist(Integer nutricionistaId, 
                                                 LocalDate fechaInicio,
                                                 LocalDate fechaFin,
                                                 Pageable pageable);
}
