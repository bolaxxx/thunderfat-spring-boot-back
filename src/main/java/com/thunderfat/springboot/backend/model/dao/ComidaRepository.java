package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Comida;

/**
 * Modern repository for Comida (Meal) operations with enhanced business queries.
 * Supports comprehensive meal management for nutrition plans.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Repository
public interface ComidaRepository extends JpaRepository<Comida, Integer> {
    
    // ================================
    // BUSINESS-SPECIFIC QUERIES
    // ================================
    
    /**
     * Find meals by diet plan ID with pagination
     */
    @Query(value = "SELECT c.* FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "WHERE d.id_plandieta = :planDietaId ORDER BY c.hora", nativeQuery = true)
    Page<Comida> findByPlanDietaId(@Param("planDietaId") Integer planDietaId, Pageable pageable);
    
    /**
     * Find meals by nutritionist ID
     */
    @Query(value = "SELECT c.* FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "JOIN plan_dieta pd ON d.id_plandieta = pd.id " +
           "JOIN paciente p ON pd.id_paciente = p.id " +
           "JOIN nutricionista n ON p.id_nutricionista = n.id " +
           "WHERE n.id = :nutricionistaId ORDER BY d.fecha DESC, c.hora", nativeQuery = true)
    Page<Comida> findByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);
    
    /**
     * Find today's meals for a patient
     */
    @Query(value = "SELECT c.* FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "JOIN plan_dieta pd ON d.id_plandieta = pd.id " +
           "WHERE pd.id_paciente = :pacienteId AND d.fecha = :fecha ORDER BY c.hora", nativeQuery = true)
    List<Comida> findTodayMeals(@Param("pacienteId") Integer pacienteId, @Param("fecha") LocalDate fecha);
    
    /**
     * Find meals by time range for a patient
     */
    @Query(value = "SELECT c.* FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "JOIN plan_dieta pd ON d.id_plandieta = pd.id " +
           "WHERE pd.id_paciente = :pacienteId " +
           "AND c.hora BETWEEN :horaInicio AND :horaFin ORDER BY c.hora", nativeQuery = true)
    List<Comida> findByTimeRange(@Param("pacienteId") Integer pacienteId, 
                                @Param("horaInicio") LocalTime horaInicio, 
                                @Param("horaFin") LocalTime horaFin);
    
    // ================================
    // ANALYTICS & REPORTING QUERIES
    // ================================
    
    /**
     * Count total meals by nutritionist
     */
    @Query(value = "SELECT COUNT(c.id) FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "JOIN plan_dieta pd ON d.id_plandieta = pd.id " +
           "JOIN paciente p ON pd.id_paciente = p.id " +
           "JOIN nutricionista n ON p.id_nutricionista = n.id " +
           "WHERE n.id = :nutricionistaId", nativeQuery = true)
    Long countByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Count active meals by nutritionist (current diet plans)
     */
    @Query(value = "SELECT COUNT(c.id) FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "JOIN plan_dieta pd ON d.id_plandieta = pd.id " +
           "JOIN paciente p ON pd.id_paciente = p.id " +
           "JOIN nutricionista n ON p.id_nutricionista = n.id " +
           "WHERE n.id = :nutricionistaId " +
           "AND pd.fechafin >= CURRENT_DATE", nativeQuery = true)
    Long countActiveMealsByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Calculate average meal calories by nutritionist
     * Note: Using native query due to HQL limitations with correlated subqueries in aggregate functions
     */
    @Query(value = "SELECT AVG(meal_calories) FROM (" +
                   "  SELECT SUM(pld.kcaltotales) as meal_calories " +
                   "  FROM platoplandieta pld " +
                   "  JOIN comidas c ON pld.platos_id = c.id " +
                   "  JOIN diadieta dd ON c.comidas_id = dd.id " +
                   "  JOIN plan_dieta pd ON dd.id_plandieta = pd.id " +
                   "  JOIN paciente p ON pd.id_paciente = p.id " +
                   "  WHERE p.id_nutricionista = :nutricionistaId " +
                   "  GROUP BY c.id" +
                   ") AS meal_totals", nativeQuery = true)
    Double getAverageMealCaloriesByNutritionist(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Find most popular meals by frequency of use
     */
    @Query(value = "SELECT c.* FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "JOIN plan_dieta pd ON d.id_plandieta = pd.id " +
           "JOIN paciente p ON pd.id_paciente = p.id " +
           "JOIN nutricionista n ON p.id_nutricionista = n.id " +
           "WHERE n.id = :nutricionistaId " +
           "GROUP BY c.id, c.hora, c.valoracion ORDER BY COUNT(c.id) DESC", nativeQuery = true)
    Page<Comida> findMostPopularMealsByNutritionist(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);
    
    /**
     * Find meals with high patient satisfaction (valoracion >= 4)
     */
    @Query(value = "SELECT c.* FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "JOIN plan_dieta pd ON d.id_plandieta = pd.id " +
           "JOIN paciente p ON pd.id_paciente = p.id " +
           "JOIN nutricionista n ON p.id_nutricionista = n.id " +
           "WHERE n.id = :nutricionistaId " +
           "AND c.valoracion >= 4 ORDER BY c.valoracion DESC", nativeQuery = true)
    Page<Comida> findHighRatedMealsByNutritionist(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);
    
    /**
     * Find meals requiring attention (low satisfaction)
     */
    @Query(value = "SELECT c.* FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "JOIN plan_dieta pd ON d.id_plandieta = pd.id " +
           "JOIN paciente p ON pd.id_paciente = p.id " +
           "JOIN nutricionista n ON p.id_nutricionista = n.id " +
           "WHERE n.id = :nutricionistaId " +
           "AND c.valoracion <= 2 ORDER BY d.fecha DESC", nativeQuery = true)
    List<Comida> findLowRatedMealsByNutritionist(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Find meals by date range for reporting
     */
    @Query(value = "SELECT c.* FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "JOIN plan_dieta pd ON d.id_plandieta = pd.id " +
           "JOIN paciente p ON pd.id_paciente = p.id " +
           "JOIN nutricionista n ON p.id_nutricionista = n.id " +
           "WHERE n.id = :nutricionistaId " +
           "AND d.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY d.fecha DESC, c.hora", nativeQuery = true)
    Page<Comida> findByDateRangeAndNutritionist(@Param("nutricionistaId") Integer nutricionistaId,
                                               @Param("fechaInicio") LocalDate fechaInicio,
                                               @Param("fechaFin") LocalDate fechaFin,
                                               Pageable pageable);
    
    // ================================
    // LEGACY SUPPORT (DEPRECATED)
    // ================================
    
    /**
     * @deprecated Use findByPlanDietaId instead
     */
    @Deprecated
    @Query(value = "SELECT c.* FROM comidas c " +
           "JOIN diadieta d ON c.comidas_id = d.id " +
           "WHERE d.id_plandieta = :planDietaId", nativeQuery = true)
    List<Comida> findByPlandieta(@Param("planDietaId") Integer planDietaId);
}
