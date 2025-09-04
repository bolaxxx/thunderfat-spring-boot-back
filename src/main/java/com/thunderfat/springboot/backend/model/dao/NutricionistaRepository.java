package com.thunderfat.springboot.backend.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Nutricionista;

/**
 * Repository interface for managing Nutricionista entities.
 * Follows Spring Boot 2025 best practices including:
 * - Pagination support for all list operations
 * - Caching for frequently accessed data  
 * - EntityGraph for optimized loading
 * - Named parameters for better readability
 * - Business validation methods
 * - Comprehensive search capabilities
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Repository
public interface NutricionistaRepository extends BaseRepository<Nutricionista, Integer> {
    
    // ================================
    // GEOGRAPHICAL DATA QUERIES
    // ================================
    
    /**
     * Finds all distinct provinces where nutritionists are located.
     * Cached for better performance as this data changes infrequently.
     * 
     * @return list of distinct provinces
     */
    @Cacheable(value = "nutricionistas", key = "'all_provinces'")
    @Query("SELECT DISTINCT n.provincia FROM Nutricionista n WHERE n.provincia IS NOT NULL ORDER BY n.provincia")
    List<String> findDistinctProvincias();
    
    /**
     * Finds all distinct localities within a specific province.
     * 
     * @param provincia the province name
     * @return list of distinct localities in the province
     */
    @Cacheable(value = "nutricionistas", key = "'localities_' + #provincia")
    @Query("SELECT DISTINCT n.localidad FROM Nutricionista n " +
           "WHERE n.provincia = :provincia AND n.localidad IS NOT NULL " +
           "ORDER BY n.localidad")
    List<String> findDistinctLocalidadesByProvincia(@Param("provincia") String provincia);
    
    /**
     * Finds nutritionists by locality with pagination support.
     * 
     * @param localidad the locality name
     * @param pageable pagination information
     * @return paginated list of nutritionists in the locality
     */
    @EntityGraph(attributePaths = {"pacientes", "roles"})
    @Query("SELECT n FROM Nutricionista n WHERE LOWER(n.localidad) = LOWER(:localidad)")
    Page<Nutricionista> findByLocalidadIgnoreCase(@Param("localidad") String localidad, Pageable pageable);
    
    /**
     * Finds nutritionists by locality (non-paginated for backward compatibility).
     * 
     * @param localidad the locality name
     * @return list of nutritionists in the locality
     */
    @Query("SELECT n FROM Nutricionista n WHERE LOWER(n.localidad) = LOWER(:localidad) ORDER BY n.nombre, n.apellidos")
    List<Nutricionista> findByLocalidadIgnoreCase(@Param("localidad") String localidad);
    
    // ================================
    // SEARCH AND FILTER METHODS
    // ================================
    
    /**
     * Comprehensive search across multiple fields (name, email, phone).
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return paginated search results
     */
    @Query("SELECT DISTINCT n FROM Nutricionista n WHERE " +
           "LOWER(CONCAT(TRIM(n.nombre), ' ', TRIM(n.apellidos))) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(n.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "n.telefono LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(n.numeroColegiadoProfesional) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Nutricionista> findByMultipleFieldsSearch(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Finds nutritionists by name (first name or last name).
     * 
     * @param name the name to search for
     * @param pageable pagination information
     * @return paginated list of nutritionists matching the name
     */
    @Query("SELECT n FROM Nutricionista n WHERE " +
           "LOWER(n.nombre) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(n.apellidos) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(CONCAT(TRIM(n.nombre), ' ', TRIM(n.apellidos))) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Nutricionista> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    /**
     * Finds nutritionists by professional registration number.
     * 
     * @param numeroColegiadoProfesional the professional registration number
     * @return optional nutritionist with the registration number
     */
    @Cacheable(value = "nutricionistas", key = "#numeroColegiadoProfesional")
    Optional<Nutricionista> findByNumeroColegiadoProfesionalIgnoreCase(String numeroColegiadoProfesional);
    
    /**
     * Finds nutritionists by email (unique constraint).
     * 
     * @param email the email address
     * @return optional nutritionist with the email
     */
    @Cacheable(value = "nutricionistas", key = "#email")
    Optional<Nutricionista> findByEmailIgnoreCase(String email);
    
    // ================================
    // BUSINESS VALIDATION METHODS
    // ================================
    
    /**
     * Checks if email exists (excluding specific nutritionist ID for update validation).
     * 
     * @param email the email to check
     * @param id the nutritionist ID to exclude
     * @return true if email exists for another nutritionist
     */
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Nutricionista n " +
           "WHERE LOWER(n.email) = LOWER(:email) AND n.id != :id")
    boolean existsByEmailIgnoreCaseAndIdNot(@Param("email") String email, @Param("id") Integer id);
    
    /**
     * Checks if professional registration number exists (excluding specific ID).
     * 
     * @param numeroColegiadoProfesional the professional number to check
     * @param id the nutritionist ID to exclude
     * @return true if number exists for another nutritionist
     */
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Nutricionista n " +
           "WHERE LOWER(n.numeroColegiadoProfesional) = LOWER(:numero) AND n.id != :id")
    boolean existsByNumeroColegiadoProfesionalIgnoreCaseAndIdNot(
        @Param("numero") String numeroColegiadoProfesional, 
        @Param("id") Integer id
    );
    
    // ================================
    // ANALYTICS AND REPORTING
    // ================================
    
    /**
     * Counts active nutritionists by province.
     * 
     * @param provincia the province name
     * @return count of active nutritionists in the province
     */
    @Query("SELECT COUNT(n) FROM Nutricionista n WHERE n.provincia = :provincia AND n.enabled = true")
    Long countActiveNutricionistasByProvincia(@Param("provincia") String provincia);
    
    /**
     * Counts total patients managed by all nutritionists.
     * 
     * @return total patient count across all nutritionists
     */
    @Query("SELECT COUNT(p) FROM Nutricionista n JOIN n.pacientes p WHERE n.enabled = true")
    Long countTotalPatientsManaged();
    
    /**
     * Finds nutritionists with patient count within range.
     * Useful for workload analysis.
     * 
     * @param minPatients minimum patient count
     * @param maxPatients maximum patient count
     * @param pageable pagination information
     * @return paginated list of nutritionists with patient count in range
     */
    @Query("SELECT n FROM Nutricionista n WHERE " +
           "SIZE(n.pacientes) BETWEEN :minPatients AND :maxPatients " +
           "AND n.enabled = true")
    Page<Nutricionista> findByPatientCountRange(
        @Param("minPatients") int minPatients,
        @Param("maxPatients") int maxPatients,
        Pageable pageable
    );
    
    /**
     * Finds nutritionists with active patients (has at least one patient).
     * 
     * @param pageable pagination information
     * @return paginated list of nutritionists with patients
     */
    @Query("SELECT DISTINCT n FROM Nutricionista n WHERE SIZE(n.pacientes) > 0 AND n.enabled = true")
    Page<Nutricionista> findNutricionistasWithPatients(Pageable pageable);
    
    /**
     * Finds available nutritionists (those with fewer than maximum patient capacity).
     * 
     * @param maxCapacity maximum patient capacity per nutritionist
     * @param pageable pagination information
     * @return paginated list of available nutritionists
     */
    @Query("SELECT n FROM Nutricionista n WHERE SIZE(n.pacientes) < :maxCapacity AND n.enabled = true")
    Page<Nutricionista> findAvailableNutricionistas(@Param("maxCapacity") int maxCapacity, Pageable pageable);
    
    // ================================
    // SPRING DATA JPA METHOD NAMING CONVENTIONS
    // ================================
    
    /**
     * Spring Data JPA method naming convention examples.
     * These methods are automatically implemented by Spring Data.
     */
    
    // Find by single field with case insensitive search
    List<Nutricionista> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
    
    // Find by province and enabled status
    Page<Nutricionista> findByProvinciaIgnoreCaseAndEnabledTrue(String provincia, Pageable pageable);
    
    // Find by locality and enabled status  
    List<Nutricionista> findByLocalidadIgnoreCaseAndEnabledTrue(String localidad);
    
    // Check existence by email (case insensitive)
    boolean existsByEmailIgnoreCase(String email);
    
    // Check existence by professional number (case insensitive)
    boolean existsByNumeroColegiadoProfesionalIgnoreCase(String numeroColegiadoProfesional);
    
    // Count by province
    Long countByProvinciaIgnoreCase(String provincia);
    
    // Count by enabled status
    Long countByEnabledTrue();
}
