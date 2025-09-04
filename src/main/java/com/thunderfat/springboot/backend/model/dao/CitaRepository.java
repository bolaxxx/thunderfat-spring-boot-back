package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Cita;

/**
 * Repository interface for managing Cita entities.
 * Follows Spring Boot 2025 best practices including:
 * - Pagination support for all list operations
 * - Caching for frequently accessed data
 * - EntityGraph for optimized loading
 * - Named parameters for better readability
 * - Business-specific queries for appointment management
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Repository
public interface CitaRepository extends BaseRepository<Cita, Integer> {
    
    /**
     * Finds appointments by patient ID with pagination.
     * Uses EntityGraph to optimize loading of related entities.
     * 
     * @param pacienteId the patient ID
     * @param pageable pagination information
     * @return paginated list of patient appointments
     */
    @Cacheable(value = "citas-by-patient", key = "#pacienteId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @EntityGraph(attributePaths = {"paciente", "nutricionista"})
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId ORDER BY c.fechaini DESC")
    Page<Cita> findByPacienteId(@Param("pacienteId") Integer pacienteId, Pageable pageable);
    
    /**
     * Finds appointments by nutritionist ID with pagination.
     * Uses caching for performance optimization.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param pageable pagination information
     * @return paginated list of nutritionist appointments
     */
    @Cacheable(value = "citas-by-nutritionist", key = "#nutricionistaId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @EntityGraph(attributePaths = {"paciente", "nutricionista"})
    @Query("SELECT c FROM Cita c WHERE c.nutricionista.id = :nutricionistaId ORDER BY c.fechaini DESC")
    Page<Cita> findByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId, Pageable pageable);
    
    /**
     * Finds appointments for a nutritionist within a specific date range.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return paginated list of appointments in date range
     */
    @Cacheable(value = "citas-by-nutritionist-dates", 
              key = "#nutricionistaId + ':' + #startDate + ':' + #endDate + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @EntityGraph(attributePaths = {"paciente", "nutricionista"})
    @Query("SELECT c FROM Cita c WHERE c.nutricionista.id = :nutricionistaId " +
           "AND DATE(c.fechaini) BETWEEN :startDate AND :endDate " +
           "ORDER BY c.fechaini ASC")
    Page<Cita> findByNutricionistaIdAndDateRange(@Param("nutricionistaId") Integer nutricionistaId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 Pageable pageable);
    
    /**
     * Finds appointments within a specific date range across all nutritionists.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return paginated list of appointments in date range
     */
    @Query("SELECT c FROM Cita c WHERE DATE(c.fechaini) BETWEEN :startDate AND :endDate " +
           "ORDER BY c.fechaini ASC")
    Page<Cita> findByDateRange(@Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              Pageable pageable);
    
    /**
     * Finds the next upcoming appointment for a patient.
     * 
     * @param pacienteId the patient ID
     * @param fromDateTime the datetime to search from
     * @return optional containing the next appointment if found
     */
    @Cacheable(value = "next-appointment", key = "#pacienteId + ':' + #fromDateTime")
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId " +
           "AND c.fechaini >= :fromDateTime " +
           "ORDER BY c.fechaini ASC")
    Optional<Cita> findNextAppointmentForPatient(@Param("pacienteId") Integer pacienteId,
                                                 @Param("fromDateTime") LocalDateTime fromDateTime);
    
    /**
     * Finds upcoming appointments for a nutritionist within specified days.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param fromDateTime the start datetime
     * @param toDateTime the end datetime
     * @param pageable pagination information
     * @return paginated list of upcoming appointments
     */
    @Query("SELECT c FROM Cita c WHERE c.nutricionista.id = :nutricionistaId " +
           "AND c.fechaini BETWEEN :fromDateTime AND :toDateTime " +
           "ORDER BY c.fechaini ASC")
    Page<Cita> findUpcomingAppointments(@Param("nutricionistaId") Integer nutricionistaId,
                                       @Param("fromDateTime") LocalDateTime fromDateTime,
                                       @Param("toDateTime") LocalDateTime toDateTime,
                                       Pageable pageable);
    
    /**
     * Finds conflicting appointments for scheduling validation.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDateTime the proposed start time
     * @param endDateTime the proposed end time
     * @return list of conflicting appointments
     */
    @Query("SELECT c FROM Cita c WHERE c.nutricionista.id = :nutricionistaId " +
           "AND ((c.fechaini BETWEEN :startDateTime AND :endDateTime) " +
           "OR (c.fechafin BETWEEN :startDateTime AND :endDateTime) " +
           "OR (c.fechaini <= :startDateTime AND c.fechafin >= :endDateTime))")
    List<Cita> findConflictingAppointments(@Param("nutricionistaId") Integer nutricionistaId,
                                          @Param("startDateTime") LocalDateTime startDateTime,
                                          @Param("endDateTime") LocalDateTime endDateTime);
    
    /**
     * Finds conflicting appointments excluding a specific appointment (for updates).
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDateTime the proposed start time
     * @param endDateTime the proposed end time
     * @param excludeId the appointment ID to exclude
     * @return list of conflicting appointments
     */
    @Query("SELECT c FROM Cita c WHERE c.nutricionista.id = :nutricionistaId " +
           "AND c.id != :excludeId " +
           "AND ((c.fechaini BETWEEN :startDateTime AND :endDateTime) " +
           "OR (c.fechafin BETWEEN :startDateTime AND :endDateTime) " +
           "OR (c.fechaini <= :startDateTime AND c.fechafin >= :endDateTime))")
    List<Cita> findConflictingAppointmentsExcluding(@Param("nutricionistaId") Integer nutricionistaId,
                                                    @Param("startDateTime") LocalDateTime startDateTime,
                                                    @Param("endDateTime") LocalDateTime endDateTime,
                                                    @Param("excludeId") Integer excludeId);
    
    /**
     * Counts total appointments for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @return total count of appointments
     */
    @Cacheable(value = "cita-stats", key = "#nutricionistaId + ':total-count'")
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.nutricionista.id = :nutricionistaId")
    Long countByNutricionistaId(@Param("nutricionistaId") Integer nutricionistaId);
    
    /**
     * Counts appointments for a patient.
     * 
     * @param pacienteId the patient ID
     * @return total count of patient appointments
     */
    @Cacheable(value = "cita-stats", key = "'patient:' + #pacienteId + ':total-count'")
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.paciente.id = :pacienteId")
    Long countByPacienteId(@Param("pacienteId") Integer pacienteId);
    
    /**
     * Counts appointments for a nutritionist within date range.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDate the start date
     * @param endDate the end date
     * @return count of appointments in date range
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.nutricionista.id = :nutricionistaId " +
           "AND DATE(c.fechaini) BETWEEN :startDate AND :endDate")
    Long countByNutricionistaIdAndDateRange(@Param("nutricionistaId") Integer nutricionistaId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    /**
     * Gets appointment statistics by status for a nutritionist.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDate the start date
     * @param endDate the end date
     * @return appointment statistics
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN c.fechaini > CURRENT_TIMESTAMP THEN 1 END) as upcoming, " +
           "COUNT(CASE WHEN c.fechaini <= CURRENT_TIMESTAMP AND c.fechafin >= CURRENT_TIMESTAMP THEN 1 END) as ongoing, " +
           "COUNT(CASE WHEN c.fechafin < CURRENT_TIMESTAMP THEN 1 END) as completed " +
           "FROM Cita c WHERE c.nutricionista.id = :nutricionistaId " +
           "AND DATE(c.fechaini) BETWEEN :startDate AND :endDate")
    Object[] getAppointmentStatistics(@Param("nutricionistaId") Integer nutricionistaId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);
    
    /**
     * Gets calendar events optimized for calendar display.
     * Uses native query for performance with date formatting.
     * 
     * @param nutricionistaId the nutritionist ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of calendar event data
     */
    @Query(value = "SELECT c.id, " +
                   "CONCAT(COALESCE(p.nombre, ''), ' ', COALESCE(p.apellidos, '')) as title, " +
                   "c.fechaini as start, " +
                   "c.fechafin as end, " +
                   "p.id as pacienteId " +
                   "FROM cita c " +
                   "LEFT JOIN paciente p ON c.id_paciente = p.id " +
                   "WHERE c.id_nutricionista = :nutricionistaId " +
                   "AND DATE(c.fechaini) BETWEEN :startDate AND :endDate " +
                   "ORDER BY c.fechaini ASC", 
           nativeQuery = true)
    List<Object[]> getCalendarEvents(@Param("nutricionistaId") Integer nutricionistaId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
    
    // =====================================
    // LEGACY METHODS (Backward Compatibility)
    // =====================================
    
    /**
     * @deprecated Use {@link #findByNutricionistaIdAndDateRange(Integer, LocalDate, LocalDate, Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    @Query(value = "SELECT * FROM cita WHERE id_nutricionista = ?1 AND fechaini BETWEEN ?2 AND ?3", nativeQuery = true)
    List<Cita> encontrarCitasNutricionistaFechas(int idNutricionista, LocalDate start, LocalDate end);
    
    /**
     * @deprecated Use {@link #findNextAppointmentForPatient(Integer, LocalDateTime)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    @Query(value = "SELECT * FROM cita WHERE id_paciente = ?1 AND fechaini >= ?2 ORDER BY fechaini ASC LIMIT 1", nativeQuery = true)
    Cita proximacita(int idPaciente, LocalDate fechaDesde);
    
    /**
     * @deprecated Use {@link #findByPacienteId(Integer, Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = ?1")
    List<Cita> buscarPorPaciente(int idPaciente);
    
    /**
     * @deprecated Use {@link #findByNutricionistaId(Integer, Pageable)} instead.
     * Legacy method for backward compatibility.
     */
    @Deprecated
    @Query("SELECT c FROM Cita c WHERE c.nutricionista.id = ?1")
    List<Cita> buscarPorNutricionista(int idNutricionista);
}
