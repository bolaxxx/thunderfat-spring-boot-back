package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.thunderfat.springboot.backend.config.CacheConfig;
import com.thunderfat.springboot.backend.exception.BusinessException;
import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.CitaRepository;
import com.thunderfat.springboot.backend.model.dto.CitaDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.CitaMapper;
import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.validation.ValidationGroups;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Modern implementation of CitaService following Spring Boot 2025 best practices.
 * Features:
 * - Constructor-based dependency injection
 * - Multi-level caching strategy
 * - Method-level security for healthcare data
 * - Comprehensive pagination support
 * - Business rule validation
 * - Transaction management
 * - Structured logging
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CitaServiceJPA implements ICitaService {
    
    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    
    // =====================================
    // MODERN CRUD OPERATIONS WITH PAGINATION
    // =====================================
    
    // Custom paginated methods (not in interface but useful for service layer)
    public Page<CitaDTO> findByPacienteIdPaginated(@NotNull @Positive Integer pacienteId, int page, int size) {
        log.info("Finding appointments for patient ID: {} with pagination [page: {}, size: {}]", pacienteId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Cita> citasPage = citaRepository.findByPacienteId(pacienteId, pageable);
        
        return citasPage.map(this::enrichCitaDTO);
    }
    
    public Page<CitaDTO> findByNutricionistaIdPaginated(@NotNull @Positive Integer nutricionistaId, int page, int size) {
        log.info("Finding appointments for nutritionist ID: {} with pagination [page: {}, size: {}]", nutricionistaId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Cita> citasPage = citaRepository.findByNutricionistaId(nutricionistaId, pageable);
        
        return citasPage.map(this::enrichCitaDTO);
    }
    
    public Page<CitaDTO> findByNutricionistaIdAndDateRangePaginated(@NotNull @Positive Integer nutricionistaId,
                                                          @NotNull LocalDate startDate,
                                                          @NotNull LocalDate endDate,
                                                          int page, int size) {
        log.info("Finding appointments for nutritionist ID: {} between {} and {} with pagination [page: {}, size: {}]", 
                 nutricionistaId, startDate, endDate, page, size);
        
        validateDateRange(startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Cita> citasPage = citaRepository.findByNutricionistaIdAndDateRange(nutricionistaId, startDate, endDate, pageable);
        
        return citasPage.map(this::enrichCitaDTO);
    }
    
    public Optional<CitaDTO> findNextAppointmentForPatientFromDateTime(@NotNull @Positive Integer pacienteId,
                                                          @NotNull LocalDateTime fromDateTime) {
        log.info("Finding next appointment for patient ID: {} from {}", pacienteId, fromDateTime);
        
        Optional<Cita> citaOpt = citaRepository.findNextAppointmentForPatient(pacienteId, fromDateTime);
        return citaOpt.map(this::enrichCitaDTO);
    }
    
    public Page<CitaDTO> findUpcomingAppointmentsPaginated(@NotNull @Positive Integer nutricionistaId, 
                                                 int days, int page, int size) {
        log.info("Finding upcoming appointments for nutritionist ID: {} within {} days", nutricionistaId, days);
        
        LocalDateTime fromDateTime = LocalDateTime.now();
        LocalDateTime toDateTime = fromDateTime.plusDays(days);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Cita> citasPage = citaRepository.findUpcomingAppointments(nutricionistaId, fromDateTime, toDateTime, pageable);
        
        return citasPage.map(this::enrichCitaDTO);
    }
    
    // =====================================
    // CRUD OPERATIONS
    // =====================================
    
    @Override
    @PostAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA') or (@securityService.canViewCita(returnObject, authentication.name))")
    public Optional<CitaDTO> findById(@NotNull @Positive Integer id) {
        log.info("Finding appointment by ID: {}", id);
        
        Optional<Cita> citaOpt = citaRepository.findById(id);
        return citaOpt.map(this::enrichCitaDTO);
    }
    
    // Custom save method for internal use (not in interface)
    @Transactional
    @Validated(ValidationGroups.Create.class)
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.CITAS_BY_PATIENT, key = "#citaDTO.pacienteId + '*'"),
        @CacheEvict(value = CacheConfig.CITAS_BY_NUTRITIONIST, key = "#citaDTO.nutricionistaId + '*'"),
        @CacheEvict(value = CacheConfig.NEXT_APPOINTMENT, key = "#citaDTO.pacienteId + '*'")
    })
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#citaDTO.nutricionistaId, authentication.name))")
    public CitaDTO saveInternal(@Valid CitaDTO citaDTO) {
        log.info("Creating new appointment for patient ID: {} with nutritionist ID: {}", 
                 citaDTO.getPacienteId(), citaDTO.getNutricionistaId());
        
        // Business validations
        validateAppointmentBusinessRules(citaDTO);
        
        // Check for scheduling conflicts
        validateNoConflicts(citaDTO);
        
        Cita cita = citaMapper.toEntity(citaDTO);
        Cita savedCita = citaRepository.save(cita);
        
        log.info("Successfully created appointment with ID: {}", savedCita.getId());
        return enrichCitaDTO(savedCita);
    }
    
    // Custom update method for internal use (not in interface)
    @Transactional
    @Validated(ValidationGroups.Update.class)
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.CITAS_BY_PATIENT, allEntries = true),
        @CacheEvict(value = CacheConfig.CITAS_BY_NUTRITIONIST, allEntries = true),
        @CacheEvict(value = CacheConfig.NEXT_APPOINTMENT, allEntries = true)
    })
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#citaDTO.nutricionistaId, authentication.name))")
    public CitaDTO updateInternal(@Valid CitaDTO citaDTO) {
        log.info("Updating appointment with ID: {}", citaDTO.getId());
        
        if (citaDTO.getId() == null) {
            throw new BusinessException("ID es requerido para actualizar una cita");
        }
        
        // Verify appointment exists
        if (!citaRepository.existsById(citaDTO.getId())) {
            throw new ResourceNotFoundException("Cita no encontrada con ID: " + citaDTO.getId());
        }
        
        // Business validations
        validateAppointmentBusinessRules(citaDTO);
        
        // Check for scheduling conflicts (excluding current appointment)
        validateNoConflictsForUpdate(citaDTO);
        
        Cita cita = citaMapper.toEntity(citaDTO);
        Cita updatedCita = citaRepository.save(cita);
        
        log.info("Successfully updated appointment with ID: {}", updatedCita.getId());
        return enrichCitaDTO(updatedCita);
    }
    
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.CITAS_BY_PATIENT, allEntries = true),
        @CacheEvict(value = CacheConfig.CITAS_BY_NUTRITIONIST, allEntries = true),
        @CacheEvict(value = CacheConfig.NEXT_APPOINTMENT, allEntries = true)
    })
    @PreAuthorize("hasRole('ADMIN') or (@securityService.canDeleteCita(#id, authentication.name))")
    public void deleteById(@NotNull @Positive Integer id) {
        log.info("Deleting appointment with ID: {}", id);
        
        if (!citaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cita no encontrada con ID: " + id);
        }
        
        citaRepository.deleteById(id);
        log.info("Successfully deleted appointment with ID: {}", id);
    }
    
    // =====================================
    // BUSINESS ANALYTICS METHODS (CUSTOM)
    // =====================================
    
    // Custom analytics methods not in interface but useful for service layer
    @Cacheable(value = CacheConfig.CITA_STATS, key = "#nutricionistaId + ':total-count'")
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#nutricionistaId, authentication.name))")
    public Long countByNutricionistaIdCustom(@NotNull @Positive Integer nutricionistaId) {
        log.debug("Counting appointments for nutritionist ID: {}", nutricionistaId);
        return citaRepository.countByNutricionistaId(nutricionistaId);
    }
    
    @Cacheable(value = CacheConfig.CITA_STATS, key = "'patient:' + #pacienteId + ':total-count'")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA') or (@securityService.isPacienteOwner(#pacienteId, authentication.name))")
    public Long countByPacienteIdCustom(@NotNull @Positive Integer pacienteId) {
        log.debug("Counting appointments for patient ID: {}", pacienteId);
        return citaRepository.countByPacienteId(pacienteId);
    }
    
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#nutricionistaId, authentication.name))")
    public Long countByNutricionistaIdAndDateRangeCustom(@NotNull @Positive Integer nutricionistaId,
                                                  @NotNull LocalDate startDate,
                                                  @NotNull LocalDate endDate) {
        log.debug("Counting appointments for nutritionist ID: {} between {} and {}", nutricionistaId, startDate, endDate);
        
        validateDateRange(startDate, endDate);
        return citaRepository.countByNutricionistaIdAndDateRange(nutricionistaId, startDate, endDate);
    }
    
    // =====================================
    // LEGACY COMPATIBILITY METHODS
    // =====================================
    
    @Override
    @Deprecated
    public List<CitaDTO> listar() {
        log.warn("Using deprecated listar() method. Consider using paginated alternatives.");
        return citaRepository.findAll().stream()
                .map(this::enrichCitaDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated
    @PostAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA') or (@securityService.canViewCita(returnObject, authentication.name))")
    public CitaDTO buscarPorId(int id) {
        log.warn("Using deprecated buscarPorId() method. Consider using findById().");
        return findById(id).orElseThrow(() -> 
            new ResourceNotFoundException("Cita no encontrada con ID: " + id));
    }
    
    @Override
    @Deprecated
    @Transactional
    public void insertar(CitaDTO citaDTO) {
        log.warn("Using deprecated insertar() method. Consider using create().");
        saveInternal(citaDTO);
    }
    
    @Override
    @Deprecated
    @Transactional
    public void eliminar(int id) {
        log.warn("Using deprecated eliminar() method. Consider using deleteById().");
        deleteById(id);
    }
    
    // =====================================
    // MISSING INTERFACE METHODS
    // =====================================
    
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    public Page<CitaDTO> findAllPaginated(Pageable pageable) {
        log.info("Finding all appointments with pagination: {}", pageable);
        
        Page<Cita> citasPage = citaRepository.findAll(pageable);
        return citasPage.map(this::enrichCitaDTO);
    }
    
    @Override
    @Transactional
    @Validated(ValidationGroups.Create.class)
    public CitaDTO create(@Valid CitaDTO citaDTO) {
        log.info("Creating new appointment via create method");
        // Ensure ID is null for creation
        citaDTO.setId(null);
        return saveInternal(citaDTO);
    }
    
    @Override
    @Transactional
    @Validated(ValidationGroups.Update.class)
    public CitaDTO update(@NotNull @Positive Integer id, @Valid CitaDTO citaDTO) {
        log.info("Updating appointment with ID: {} via update method", id);
        citaDTO.setId(id);
        return updateInternal(citaDTO);
    }
    
    @Override
    public boolean existsById(@NotNull @Positive Integer id) {
        log.debug("Checking if appointment exists with ID: {}", id);
        return citaRepository.existsById(id);
    }
    
    @Override
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#nutricionistaId, authentication.name))")
    public Page<CitaDTO> findByNutricionistaId(@NotNull @Positive Integer nutricionistaId, Pageable pageable) {
        log.info("Finding appointments for nutritionist ID: {} with pageable: {}", nutricionistaId, pageable);
        
        Page<Cita> citasPage = citaRepository.findByNutricionistaId(nutricionistaId, pageable);
        return citasPage.map(this::enrichCitaDTO);
    }
    
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA') or (@securityService.isPacienteOwner(#pacienteId, authentication.name))")
    public Page<CitaDTO> findByPacienteId(@NotNull @Positive Integer pacienteId, Pageable pageable) {
        log.info("Finding appointments for patient ID: {} with pageable: {}", pacienteId, pageable);
        
        Page<Cita> citasPage = citaRepository.findByPacienteId(pacienteId, pageable);
        return citasPage.map(this::enrichCitaDTO);
    }
    
    @Override
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#nutricionistaId, authentication.name))")
    public Page<CitaDTO> findByNutricionistaIdAndDateRange(@NotNull @Positive Integer nutricionistaId,
                                                          @NotNull LocalDate startDate,
                                                          @NotNull LocalDate endDate,
                                                          Pageable pageable) {
        log.info("Finding appointments for nutritionist ID: {} between {} and {} with pageable: {}", 
                 nutricionistaId, startDate, endDate, pageable);
        
        validateDateRange(startDate, endDate);
        
        Page<Cita> citasPage = citaRepository.findByNutricionistaIdAndDateRange(nutricionistaId, startDate, endDate, pageable);
        return citasPage.map(this::enrichCitaDTO);
    }
    
    @Override
    public Page<CitaDTO> findByDateRange(@NotNull LocalDate startDate, @NotNull LocalDate endDate, Pageable pageable) {
        log.info("Finding appointments between {} and {} with pageable: {}", startDate, endDate, pageable);
        
        validateDateRange(startDate, endDate);
        
        Page<Cita> citasPage = citaRepository.findByDateRange(startDate, endDate, pageable);
        return citasPage.map(this::enrichCitaDTO);
    }
    
    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA') or (@securityService.isPacienteOwner(#pacienteId, authentication.name))")
    public Optional<CitaDTO> findNextAppointmentForPatient(@NotNull @Positive Integer pacienteId, @NotNull LocalDate fromDate) {
        log.info("Finding next appointment for patient ID: {} from date: {}", pacienteId, fromDate);
        
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        return findNextAppointmentForPatientFromDateTime(pacienteId, fromDateTime);
    }
    
    @Override
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#nutricionistaId, authentication.name))")
    public Page<CitaDTO> findUpcomingAppointments(@NotNull @Positive Integer nutricionistaId, 
                                                 @NotNull Integer days, 
                                                 Pageable pageable) {
        log.info("Finding upcoming appointments for nutritionist ID: {} within {} days with pageable: {}", 
                 nutricionistaId, days, pageable);
        
        LocalDateTime fromDateTime = LocalDateTime.now();
        LocalDateTime toDateTime = fromDateTime.plusDays(days);
        
        Page<Cita> citasPage = citaRepository.findUpcomingAppointments(nutricionistaId, fromDateTime, toDateTime, pageable);
        return citasPage.map(this::enrichCitaDTO);
    }
    
    @Override
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#nutricionistaId, authentication.name))")
    public List<CitaDTO> findConflictingAppointments(@NotNull @Positive Integer nutricionistaId,
                                                    @NotNull LocalDateTime startDateTime,
                                                    @NotNull LocalDateTime endDateTime,
                                                    Integer excludeId) {
        log.info("Finding conflicting appointments for nutritionist ID: {} between {} and {}", 
                 nutricionistaId, startDateTime, endDateTime);
        
        List<Cita> conflicts;
        if (excludeId != null) {
            conflicts = citaRepository.findConflictingAppointmentsExcluding(
                nutricionistaId, startDateTime, endDateTime, excludeId);
        } else {
            conflicts = citaRepository.findConflictingAppointments(
                nutricionistaId, startDateTime, endDateTime);
        }
        
        return conflicts.stream()
                .map(this::enrichCitaDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(value = CacheConfig.CALENDAR_EVENTS, key = "#nutricionistaId + ':' + #startDate + ':' + #endDate")
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#nutricionistaId, authentication.name))")
    public List<Map<String, Object>> getCalendarEvents(@NotNull @Positive Integer nutricionistaId,
                                                      @NotNull LocalDate startDate,
                                                      @NotNull LocalDate endDate) {
        log.info("Getting calendar events for nutritionist ID: {} between {} and {}", 
                 nutricionistaId, startDate, endDate);
        
        validateDateRange(startDate, endDate);
        List<Object[]> rawResults = citaRepository.getCalendarEvents(nutricionistaId, startDate, endDate);
        
        return rawResults.stream()
                .map(row -> {
                    Map<String, Object> event = new java.util.HashMap<>();
                    event.put("id", row[0]);
                    event.put("title", row[1]);
                    event.put("start", row[2]);
                    event.put("end", row[3]);
                    event.put("pacienteId", row[4]);
                    return event;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Cacheable(value = CacheConfig.CITA_STATS, key = "#nutricionistaId + ':stats:' + #startDate + ':' + #endDate")
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#nutricionistaId, authentication.name))")
    public Map<String, Object> getAppointmentStatistics(@NotNull @Positive Integer nutricionistaId,
                                                       @NotNull LocalDate startDate,
                                                       @NotNull LocalDate endDate) {
        log.info("Getting appointment statistics for nutritionist ID: {} between {} and {}", 
                 nutricionistaId, startDate, endDate);
        
        validateDateRange(startDate, endDate);
        Object[] rawStats = citaRepository.getAppointmentStatistics(nutricionistaId, startDate, endDate);
        
        Map<String, Object> stats = new java.util.HashMap<>();
        if (rawStats != null && rawStats.length >= 3) {
            stats.put("upcoming", rawStats[0]);
            stats.put("ongoing", rawStats[1]);
            stats.put("completed", rawStats[2]);
        } else {
            stats.put("upcoming", 0L);
            stats.put("ongoing", 0L);
            stats.put("completed", 0L);
        }
        
        return stats;
    }
    
    @Override
    @Cacheable(value = CacheConfig.CITA_STATS, key = "'patient:' + #pacienteId + ':count'")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA') or (@securityService.isPacienteOwner(#pacienteId, authentication.name))")
    public Long countAppointmentsByPacienteId(@NotNull @Positive Integer pacienteId) {
        log.debug("Counting appointments for patient ID: {}", pacienteId);
        return countByPacienteIdCustom(pacienteId);
    }
    
    @Override
    @Cacheable(value = CacheConfig.CITA_STATS, key = "#nutricionistaId + ':count'")
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#nutricionistaId, authentication.name))")
    public Long countAppointmentsByNutricionistaId(@NotNull @Positive Integer nutricionistaId) {
        log.debug("Counting appointments for nutritionist ID: {}", nutricionistaId);
        return countByNutricionistaIdCustom(nutricionistaId);
    }
    
    @Override
    public boolean validateAppointmentScheduling(@Valid CitaDTO citaDTO) {
        log.info("Validating appointment scheduling for patient ID: {} with nutritionist ID: {}", 
                 citaDTO.getPacienteId(), citaDTO.getNutricionistaId());
        
        try {
            validateAppointmentBusinessRules(citaDTO);
            
            if (citaDTO.getId() == null) {
                validateNoConflicts(citaDTO);
            } else {
                validateNoConflictsForUpdate(citaDTO);
            }
            
            return true;
        } catch (BusinessException e) {
            log.warn("Appointment validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    // =====================================
    // LEGACY COMPATIBILITY METHODS (CONTINUED)
    // =====================================
    
    @Override
    @Deprecated
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#idNutricionista, authentication.name))")
    public List<CitaDTO> buscarPorNutricionista(int idNutricionista) {
        log.warn("Using deprecated buscarPorNutricionista() method. Consider using findByNutricionistaId().");
        return citaRepository.buscarPorNutricionista(idNutricionista).stream()
                .map(this::enrichCitaDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA') or (@securityService.isPacienteOwner(#idPaciente, authentication.name))")
    public List<CitaDTO> buscarPorPaciente(int idPaciente) {
        log.warn("Using deprecated buscarPorPaciente() method. Consider using findByPacienteId().");
        return citaRepository.buscarPorPaciente(idPaciente).stream()
                .map(this::enrichCitaDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isNutricionistaOwner(#idNutricionista, authentication.name))")
    public List<Map<String, Object>> listarPorNutricionistaEntreFechas(int idNutricionista, LocalDate start, LocalDate end) {
        log.warn("Using deprecated listarPorNutricionistaEntreFechas() method. Consider using findByNutricionistaIdAndDateRange().");
        List<Cita> citas = citaRepository.encontrarCitasNutricionistaFechas(idNutricionista, start, end);
        
        return citas.stream()
                .map(cita -> {
                    Map<String, Object> citaMap = new java.util.HashMap<>();
                    citaMap.put("id", cita.getId());
                    citaMap.put("fechaInicio", cita.getFechaini());
                    citaMap.put("fechaFin", cita.getFechafin());
                    citaMap.put("pacienteId", cita.getPaciente() != null ? cita.getPaciente().getId() : null);
                    citaMap.put("nutricionistaId", cita.getNutricionista() != null ? cita.getNutricionista().getId() : null);
                    return citaMap;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Deprecated
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA') or (@securityService.isPacienteOwner(#idPaciente, authentication.name))")
    public CitaDTO buscarProximaCita(int idPaciente, LocalDate fechaDesde) {
        log.warn("Using deprecated buscarProximaCita() method. Consider using findNextAppointmentForPatient().");
        Cita cita = citaRepository.proximacita(idPaciente, fechaDesde);
        return cita != null ? enrichCitaDTO(cita) : null;
    }
    
    // =====================================
    // BUSINESS VALIDATION METHODS
    // =====================================
    
    private void validateAppointmentBusinessRules(CitaDTO citaDTO) {
        // Validate time range
        if (!citaDTO.isValidTimeRange()) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        // Validate minimum duration (e.g., 15 minutes)
        Long duration = citaDTO.calculateDuration();
        if (duration != null && duration < 15) {
            throw new BusinessException("La duración mínima de una cita es de 15 minutos");
        }
        
        // Validate maximum duration (e.g., 4 hours)
        if (duration != null && duration > 240) {
            throw new BusinessException("La duración máxima de una cita es de 4 horas");
        }
        
        // Validate not in the past (for new appointments)
        if (citaDTO.getId() == null && citaDTO.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new BusinessException("No se pueden crear citas en el pasado");
        }
    }
    
    private void validateNoConflicts(CitaDTO citaDTO) {
        List<Cita> conflicts = citaRepository.findConflictingAppointments(
            citaDTO.getNutricionistaId(),
            citaDTO.getFechaInicio(),
            citaDTO.getFechaFin()
        );
        
        if (!conflicts.isEmpty()) {
            throw new BusinessException("Ya existe una cita programada en ese horario");
        }
    }
    
    private void validateNoConflictsForUpdate(CitaDTO citaDTO) {
        List<Cita> conflicts = citaRepository.findConflictingAppointmentsExcluding(
            citaDTO.getNutricionistaId(),
            citaDTO.getFechaInicio(),
            citaDTO.getFechaFin(),
            citaDTO.getId()
        );
        
        if (!conflicts.isEmpty()) {
            throw new BusinessException("Ya existe una cita programada en ese horario");
        }
    }
    
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
    }
    
    private CitaDTO enrichCitaDTO(Cita cita) {
        CitaDTO dto = citaMapper.toDto(cita);
        
        // Enrich with computed fields
        dto.setDuracionMinutos(dto.calculateDuration());
        dto.setEstado(dto.determineStatus());
        
        // Enrich with related entity names if available
        if (cita.getPaciente() != null) {
            dto.setPacienteNombre(cita.getPaciente().getNombre() + " " + 
                                 (cita.getPaciente().getApellidos() != null ? cita.getPaciente().getApellidos() : ""));
        }
        
        if (cita.getNutricionista() != null) {
            dto.setNutricionistaNombre(cita.getNutricionista().getNombre() + " " + 
                                     (cita.getNutricionista().getApellidos() != null ? cita.getNutricionista().getApellidos() : ""));
        }
        
        return dto;
    }
}
