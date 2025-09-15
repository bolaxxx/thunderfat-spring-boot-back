package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Alimento;

/**
 * Repository interface for performing CRUD operations on Alimento entity.
 * Enhanced with Spring Boot 2025 best practices and custom queries
 */
@Repository
public interface AlimentoRepository extends JpaRepository<Alimento, Integer> {
	
    /**
     * Business rule: Check for duplicate names (case-insensitive)
     */
    boolean existsByNombreIgnoreCase(String nombre);
    
    /**
     * Business rule: Check for duplicate names excluding specific ID (for updates)
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Alimento a " +
           "WHERE LOWER(a.nombre) = LOWER(:nombre) AND a.id != :id")
    boolean existsByNombreIgnoreCaseAndIdNot(@Param("nombre") String nombre, @Param("id") Integer id);
    
    /**
     * Search foods by name (case-insensitive, partial match)
     */
    @Query("SELECT a FROM Alimento a WHERE LOWER(a.nombre) LIKE LOWER(FUNCTION('CONCAT', '%', :nombre, '%'))")
    Page<Alimento> findByNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);
    
    /**
     * Find foods by estado (status)
     */
    Page<Alimento> findByEstadoIgnoreCase(String estado, Pageable pageable);
    
    /**
     * Find foods by calorie range
     */
    @Query("SELECT a FROM Alimento a WHERE a.cal BETWEEN :minCal AND :maxCal")
    Page<Alimento> findByCalorieRange(@Param("minCal") Double minCal, @Param("maxCal") Double maxCal, Pageable pageable);
    
    /**
     * Find high-protein foods (protein > specified threshold)
     */
    @Query("SELECT a FROM Alimento a WHERE a.proteinas > :threshold ORDER BY a.proteinas DESC")
    List<Alimento> findHighProteinFoods(@Param("threshold") Double threshold);
    
    /**
     * Find low-calorie foods (calories < specified threshold)
     */
    @Query("SELECT a FROM Alimento a WHERE a.cal < :threshold ORDER BY a.cal ASC")
    List<Alimento> findLowCalorieFoods(@Param("threshold") Double threshold);
    
    /**
     * Find foods rich in specific vitamin
     */
    @Query("SELECT a FROM Alimento a WHERE " +
           "(:vitamin = 'A' AND a.vitamina > :threshold) OR " +
           "(:vitamin = 'B1' AND a.vitaminb1 > :threshold) OR " +
           "(:vitamin = 'B2' AND a.vitaminb2 > :threshold) OR " +
           "(:vitamin = 'C' AND a.vitaminc > :threshold)")
    List<Alimento> findFoodsRichInVitamin(@Param("vitamin") String vitamin, @Param("threshold") Double threshold);
    
    /**
     * Get nutritional summary statistics
     */
    @Query("SELECT " +
           "AVG(a.cal) as avgCalories, " +
           "MIN(a.cal) as minCalories, " +
           "MAX(a.cal) as maxCalories, " +
           "AVG(a.proteinas) as avgProteins, " +
           "AVG(a.grasas) as avgFats, " +
           "AVG(a.hidratosdecarbono) as avgCarbs " +
           "FROM Alimento a")
    Object[] getNutritionalStatistics();
    
    /**
     * Find foods suitable for diet plans (balanced nutrition)
     */
    @Query("SELECT a FROM Alimento a WHERE " +
           "a.cal BETWEEN :minCal AND :maxCal AND " +
           "a.proteinas >= :minProtein AND " +
           "a.grasas <= :maxFat")
    Page<Alimento> findDietSuitableFoods(
        @Param("minCal") Double minCal,
        @Param("maxCal") Double maxCal,
        @Param("minProtein") Double minProtein,
        @Param("maxFat") Double maxFat,
        Pageable pageable
    );
}
