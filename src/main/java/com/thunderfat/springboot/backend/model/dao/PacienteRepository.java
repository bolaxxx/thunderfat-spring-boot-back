package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Paciente;

/**
 * Repository interface for managing Paciente entities.
 * Follows Spring Boot 2025 best practices including:
 * - Pagination support for all list operations
 * - Caching for frequently accessed data
 * - EntityGraph for optimized loading
 * - Named parameters for better readability
 * - Proper documentation and business rules
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Repository
public interface PacienteRepository extends BaseRepository<Paciente, Integer> {
    
    /**
     * Finds all patients for a specific nutritionist with pagination.
     * Uses caching to improve performance for frequently accessed data.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated list of patients
     */
    @Cacheable(value = "pacientes", key = "#nutricionistaId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @Query("SELECT p FROM Paciente p WHERE p.nutricionista.id = :nutricionistaId")
    Page<Paciente> findByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);
    
    /**
     * Finds patients by nutritionist ID (non-paginated for backward compatibility).
     * Consider migrating to paginated version.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return list of patients
     */
    @Query("SELECT p FROM Paciente p WHERE p.nutricionista.id = :nutricionistaId ORDER BY p.nombre, p.apellidos")
    List<Paciente> findByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Searches patients by DNI with partial matching.
     * Uses EntityGraph to optimize loading of related entities.
     * 
     * @param dni partial DNI to search for
     * @param nutricionistaId the nutritionist ID to limit search scope
     * @param pageable pagination information
     * @return paginated search results
     */
    @EntityGraph(attributePaths = {"nutricionista", "roles"})
    @Query("SELECT p FROM Paciente p WHERE " +
           "LOWER(p.dni) LIKE LOWER(CONCAT('%', :dni, '%')) " +
           "AND p.nutricionista.id = :nutricionistaId")
    Page<Paciente> findByDniContainingIgnoreCase(
        @Param("dni") String dni, 
        @Param("nutricionistaId") Integer nutricionistaId,
        Pageable pageable
    );
    
    /**
     * Searches patients by phone number with partial matching.
     * 
     * @param telefono partial phone number to search for
     * @param nutricionistaId the nutritionist ID to limit search scope
     * @param pageable pagination information
     * @return paginated search results
     */
    @Query("SELECT p FROM Paciente p WHERE " +
           "p.telefono LIKE CONCAT('%', :telefono, '%') " +
           "AND p.nutricionista.id = :nutricionistaId")
    Page<Paciente> findByTelefonoContaining(
        @Param("telefono") String telefono, 
        @Param("nutricionistaId") Integer nutricionistaId,
        Pageable pageable
    );
    
    /**
     * Advanced search by name (first name + last name concatenated).
     * Supports partial matching across concatenated full name.
     * 
     * @param searchString the search term
     * @param nutricionistaId the nutritionist ID to limit search scope
     * @param pageable pagination information
     * @return paginated search results
     */
    @Query("SELECT p FROM Paciente p WHERE " +
           "LOWER(CONCAT(TRIM(p.nombre), ' ', TRIM(p.apellidos))) " +
           "LIKE LOWER(CONCAT('%', :searchString, '%')) " +
           "AND p.nutricionista.id = :nutricionistaId")
    Page<Paciente> findByFullNameContainingIgnoreCase(
        @Param("searchString") String searchString, 
        @Param("nutricionistaId") Integer nutricionistaId,
        Pageable pageable
    );
    
    /**
     * Combined search across multiple fields (name, DNI, phone).
     * Provides comprehensive search capability for patient lookup.
     * 
     * @param searchTerm the term to search across multiple fields
     * @param nutricionistaId the nutritionist ID to limit search scope
     * @param pageable pagination information
     * @return paginated search results
     */
    @Query("SELECT DISTINCT p FROM Paciente p WHERE p.nutricionista.id = :nutricionistaId AND (" +
           "LOWER(CONCAT(TRIM(p.nombre), ' ', TRIM(p.apellidos))) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.dni) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "p.telefono LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
           ")")
    Page<Paciente> findByMultipleFieldsSearch(
        @Param("searchTerm") String searchTerm,
        @Param("nutricionistaId") Integer nutricionistaId,
        Pageable pageable
    );
    
    /**
     * Finds patients with appointments in a specific date range.
     * Useful for scheduling and appointment management.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated list of patients with appointments in date range
     */
    @Query("SELECT DISTINCT p FROM Paciente p " +
           "JOIN p.citas c " +
           "WHERE c.fechaini BETWEEN :startDate AND :endDate " +
           "AND p.nutricionista.id = :nutricionistaId")
    Page<Paciente> findWithAppointmentsBetweenDates(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("nutricionistaId") Integer nutricionistaId,
        Pageable pageable
    );
    
    /**
     * Finds a patient by email (unique constraint).
     * Uses Optional to handle potential null results safely.
     * 
     * @param email the patient's email
     * @return optional containing the patient if found
     */
    @Cacheable(value = "pacientes", key = "#email")
    Optional<Paciente> findByEmailIgnoreCase(String email);
    
    /**
     * Checks if a patient exists for a specific nutritionist.
     * More efficient than loading the full entity.
     * 
     * @param pacienteId the patient ID
     * @param nutricionistaId the nutritionist ID
     * @return true if the patient belongs to the nutritionist
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Paciente p WHERE p.id = :pacienteId AND p.nutricionista.id = :nutricionistaId")
    boolean existsByIdAndNutricionistaId(
        @Param("pacienteId") Integer pacienteId, 
        @Param("nutricionistaId") Integer nutricionistaId
    );
    
    /**
     * Counts active patients for a nutritionist.
     * Useful for dashboard statistics.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return count of active patients
     */
    @Query("SELECT COUNT(p) FROM Paciente p WHERE p.nutricionista.id = :nutricionistaId AND p.enabled = true")
    Long countActivePatientsByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Bulk update operation to change nutritionist for multiple patients.
     * Uses @Modifying annotation for write operations.
     * 
     * @param oldNutricionistaId the current nutritionist ID
     * @param newNutricionistaId the new nutritionist ID
     * @return number of updated records
     */
    @Modifying
    @Query("UPDATE Paciente p SET p.nutricionista.id = :newNutricionistaId " +
           "WHERE p.nutricionista.id = :oldNutricionistaId")
    int transferPatientsToNewNutritionist(
        @Param("oldNutricionistaId") Integer oldNutricionistaId,
        @Param("newNutricionistaId") Integer newNutricionistaId
    );
    
    /**
     * Spring Data JPA method naming convention examples.
     * These methods are automatically implemented by Spring Data.
     */
    
    // Find by birth date range
    List<Paciente> findByFechanacimientoBetweenAndNutricionistaIdOrderByFechanacimientoDesc(
        LocalDate startDate, LocalDate endDate, Integer nutricionistaId
    );
    
    // Find by locality and nutritionist
    Page<Paciente> findByLocalidadIgnoreCaseAndNutricionistaId(
        String localidad, Integer nutricionistaId, Pageable pageable
    );
    
    // Check if email exists (excluding specific patient ID for update validation)
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);
    
    // Check if DNI exists (excluding specific patient ID for update validation)
    boolean existsByDniIgnoreCaseAndIdNot(String dni, Integer id);
}
