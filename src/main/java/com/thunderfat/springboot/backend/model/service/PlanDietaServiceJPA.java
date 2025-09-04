package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.BusinessException;
import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dto.NutricionistaStatsDTO;
import com.thunderfat.springboot.backend.model.dto.PlanDietaDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.FiltroAlimentarioMapper;
import com.thunderfat.springboot.backend.model.dto.mapper.PlanDietaMapper;
import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;
import com.thunderfat.springboot.backend.model.entity.Ingrediente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.dao.FiltroAlimentarioRepository;
import com.thunderfat.springboot.backend.model.dao.NutricionistaRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dao.PlanDietaRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * JPA implementation of the PlanDieta service interface.
 * Follows Spring Boot 2025 best practices including:
 * - DTO pattern for data transfer
 * - Caching strategies
 * - Comprehensive business validation
 * - Transaction management
 * - Pagination support
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Service("planDietaServiceJPA")
@Transactional
@Slf4j
public class PlanDietaServiceJPA implements IPlanDietaService {

    @Autowired
    private PlanDietaRepository repo;
    
    @Autowired
    private NutricionistaRepository nutricionistaRepo;
    
    @Autowired
    private PacienteRepository pacienteRepo;
    
    @Autowired
    private FiltroAlimentarioRepository filtroRepo;
    
    @Autowired
    private PlanDietaMapper mapper;
    
    @Autowired
    private FiltroAlimentarioMapper filtroMapper;

    // ================================
    // CORE CRUD OPERATIONS
    // ================================

    @Override
    @Transactional
    @CacheEvict(value = {"plandieta", "plandieta-stats", "plandieta-by-paciente", "plandieta-by-nutricionista"}, allEntries = true)
    public PlanDietaDTO createPlan(PlanDietaDTO planDietaDTO, Integer nutricionistaId, Integer pacienteId) {
        log.info("Creating new diet plan for patient: {} by nutritionist: {}", pacienteId, nutricionistaId);

        // Validate inputs
        if (nutricionistaId == null || nutricionistaId <= 0) {
            throw new BusinessException("Invalid nutritionist ID");
        }
        if (pacienteId == null || pacienteId <= 0) {
            throw new BusinessException("Invalid patient ID");
        }
        if (planDietaDTO == null) {
            throw new BusinessException("Diet plan data cannot be null");
        }
        
        // Set the IDs in the DTO to ensure consistency
        planDietaDTO.setIdNutricionista(nutricionistaId);
        planDietaDTO.setIdPaciente(pacienteId);
        
        // Verify the nutritionist and patient exist and are related
        if (!nutricionistaRepo.existsById(nutricionistaId)) {
            throw new ResourceNotFoundException("Nutritionist not found with ID: " + nutricionistaId);
        }
        if (!pacienteRepo.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + pacienteId);
        }
        
        // Check if the patient belongs to the nutritionist
        if (!pacienteRepo.existsByIdAndNutricionistaId(pacienteId, nutricionistaId)) {
            throw new BusinessException("Patient does not belong to the specified nutritionist");
        }

        // Business validation for the plan details
        validatePlanDietaCreation(planDietaDTO);
        
        // Convert to entity
        PlanDieta entity = mapper.toEntity(planDietaDTO);
        
        // Set filter relationship if provided
        if (planDietaDTO.getFiltroAplicadoId() != null) {
            FiltroAlimentario filtro = filtroRepo.findById(planDietaDTO.getFiltroAplicadoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Filtro alimentario not found with ID: " + planDietaDTO.getFiltroAplicadoId()));
            entity.setFiltrosaplicado(filtro);
        }

        // Save the entity and handle any exceptions
        try {
            // Save the entity
            PlanDieta savedEntity = repo.save(entity);
            log.debug("Saved diet plan with ID: {}", savedEntity.getId());
            
            // Convert back to DTO with populated relationships
            PlanDietaDTO resultDTO = mapper.toDTO(savedEntity);
            if (savedEntity.getFiltrosaplicado() != null) {
                resultDTO.setFiltroAplicado(filtroMapper.toDTO(savedEntity.getFiltrosaplicado()));
            }
            
            log.info("Successfully created diet plan with ID: {}", resultDTO.getId());
            return resultDTO;
        } catch (Exception e) {
            log.error("Error creating diet plan: {}", e.getMessage(), e);
            throw new BusinessException("Failed to create diet plan: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"plandieta", "plandieta-stats", "plandieta-by-paciente", "plandieta-by-nutricionista", "plandieta-current-active"}, allEntries = true)
    public PlanDietaDTO updatePlan(Integer id, PlanDietaDTO planDietaDTO, Integer nutricionistaId, Integer pacienteId) {
        log.info("Updating diet plan {} for patient: {} by nutritionist: {}", id, pacienteId, nutricionistaId);

        // Validate inputs
        if (id == null || id <= 0) {
            throw new BusinessException("Invalid diet plan ID");
        }
        if (nutricionistaId == null || nutricionistaId <= 0) {
            throw new BusinessException("Invalid nutritionist ID");
        }
        if (pacienteId == null || pacienteId <= 0) {
            throw new BusinessException("Invalid patient ID");
        }
        if (planDietaDTO == null) {
            throw new BusinessException("Diet plan data cannot be null");
        }

        // Verify the plan exists
        PlanDieta existingEntity = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diet plan not found with ID: " + id));
                
        // Verify the nutritionist and patient exist and are related
        if (!nutricionistaRepo.existsById(nutricionistaId)) {
            throw new ResourceNotFoundException("Nutritionist not found with ID: " + nutricionistaId);
        }
        if (!pacienteRepo.existsById(pacienteId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + pacienteId);
        }
        
        // Check if the patient belongs to the nutritionist
        if (!pacienteRepo.existsByIdAndNutricionistaId(pacienteId, nutricionistaId)) {
            throw new BusinessException("Patient does not belong to the specified nutritionist");
        }

        // Set IDs from parameters to ensure consistency
        planDietaDTO.setId(id);
        planDietaDTO.setIdNutricionista(nutricionistaId);
        planDietaDTO.setIdPaciente(pacienteId);

        // Business validation
        validatePlanDietaUpdate(planDietaDTO, existingEntity);

        // Convert to entity
        PlanDieta entity = mapper.toEntity(planDietaDTO);
        
        // Set filter relationship if provided
        if (planDietaDTO.getFiltroAplicadoId() != null) {
            FiltroAlimentario filtro = filtroRepo.findById(planDietaDTO.getFiltroAplicadoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Filtro alimentario not found with ID: " + planDietaDTO.getFiltroAplicadoId()));
            entity.setFiltrosaplicado(filtro);
        }

        // Save the entity and handle any exceptions
        try {
            PlanDieta savedEntity = repo.save(entity);
            log.debug("Updated diet plan with ID: {}", savedEntity.getId());
            
            // Convert back to DTO with populated relationships
            PlanDietaDTO resultDTO = mapper.toDTO(savedEntity);
            if (savedEntity.getFiltrosaplicado() != null) {
                resultDTO.setFiltroAplicado(filtroMapper.toDTO(savedEntity.getFiltrosaplicado()));
            }
            
            log.info("Successfully updated diet plan with ID: {}", resultDTO.getId());
            return resultDTO;
        } catch (Exception e) {
            log.error("Error updating diet plan: {}", e.getMessage(), e);
            throw new BusinessException("Failed to update diet plan: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta", key = "#id")
    public Optional<PlanDietaDTO> findById(Integer id) {
        log.debug("Finding diet plan by ID: {}", id);
        
        return repo.findById(id)
                .map(entity -> {
                    PlanDietaDTO dto = mapper.toDTO(entity);
                    if (entity.getFiltrosaplicado() != null) {
                        dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
                    }
                    return dto;
                });
    }

    @Override
    @Transactional
    @CacheEvict(value = {"plandieta", "plandieta-stats", "plandieta-by-paciente", "plandieta-by-nutricionista", "plandieta-current-active", "plandieta-latest"}, allEntries = true)
    public void deleteById(Integer id) {
        log.info("Deleting diet plan with ID: {}", id);
        
        // Validate input
        if (id == null || id <= 0) {
            throw new BusinessException("Invalid diet plan ID");
        }
        
        // Verify the plan exists and fetch it to get related entity IDs for cache eviction
        PlanDieta planDieta = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diet plan not found with ID: " + id));
        
        // Get related entity IDs for logging
        Integer pacienteId = planDieta.getId_paciente();
        Integer nutricionistaId = planDieta.getId_nutricionista();
        
        log.debug("Deleting diet plan {} associated with patient {} and nutritionist {}", 
                 id, pacienteId, nutricionistaId);
        
        try {
            // Delete the plan
            repo.deleteById(id);
            log.info("Successfully deleted diet plan with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting diet plan: {}", e.getMessage(), e);
            throw new BusinessException("Failed to delete diet plan: " + e.getMessage(), e);
        }
    }

    // ================================
    // NUTRITIONIST-BASED OPERATIONS
    // ================================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-by-nutricionista", key = "#nutricionistaId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PlanDietaDTO> findByNutricionistaId(Integer nutricionistaId, Pageable pageable) {
        log.debug("Finding diet plans for nutritionist: {} with pagination", nutricionistaId);
        
        Page<PlanDieta> entityPage = repo.findByNutricionistaId(nutricionistaId, pageable);
        return entityPage.map(entity -> {
            PlanDietaDTO dto = mapper.toDTO(entity);
            if (entity.getFiltrosaplicado() != null) {
                dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
            }
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-by-nutricionista", key = "#nutricionistaId + '_all'")
    public List<PlanDietaDTO> findByNutricionistaId(Integer nutricionistaId) {
        log.debug("Finding all diet plans for nutritionist: {}", nutricionistaId);
        
        List<PlanDieta> entities = repo.findByNutricionistaId(nutricionistaId);
        return entities.stream()
                .map(entity -> {
                    PlanDietaDTO dto = mapper.toDTO(entity);
                    if (entity.getFiltrosaplicado() != null) {
                        dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-active-by-nutricionista", key = "#nutricionistaId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PlanDietaDTO> findActiveByNutricionistaId(Integer nutricionistaId, Pageable pageable) {
        log.debug("Finding active diet plans for nutritionist: {} with pagination", nutricionistaId);
        
        LocalDate today = LocalDate.now();
        Page<PlanDieta> entityPage = repo.findActiveByNutricionistaId(nutricionistaId, today, pageable);
        return entityPage.map(entity -> {
            PlanDietaDTO dto = mapper.toDTO(entity);
            if (entity.getFiltrosaplicado() != null) {
                dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
            }
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-stats", key = "#nutricionistaId", 
              condition = "#nutricionistaId != null && #nutricionistaId > 0",
              unless = "#result == null")
    public NutricionistaStatsDTO getNutricionistaStatistics(Integer nutricionistaId) {
        log.debug("Getting statistics for nutritionist: {}", nutricionistaId);
        
        LocalDate today = LocalDate.now();
        
        // Use repository methods for aggregation calculations
        Long totalPlans = repo.countByNutricionistaId(nutricionistaId);
        Long activePlans = repo.countActiveByNutricionistaId(nutricionistaId, today);
        
        // Calculate expired plans directly from the database
        Long expiredPlans = repo.countExpiredByNutricionistaId(nutricionistaId, today);
        
        // Calculate real average duration instead of using default
        Double averageDuration = repo.calculateAveragePlanDuration(nutricionistaId);
        if (averageDuration == null) {
            averageDuration = 30.0; // fallback to default if no plans
        }
        
        // Use optimized repository method with @EntityGraph to fetch distinct patient IDs efficiently
        Set<Integer> distinctPatientIds = repo.findDistinctPatientIdsByNutricionistaId(nutricionistaId);
        Long totalPatients = (long) distinctPatientIds.size();
        
        // Use optimized repository method with @EntityGraph for active patients
        Set<Integer> activePatientIds = repo.findDistinctActivePatientIdsByNutricionistaId(nutricionistaId, today);
        Long activePatientsCount = (long) activePatientIds.size();
        
        // Use repository method for expiring plans count
        LocalDate weekFromNow = today.plusDays(7);
        Long plansSoonToExpire = repo.countExpiringSoonByNutricionistaId(nutricionistaId, today, weekFromNow);
        
        // Build and return statistics DTO with calculated values
        NutricionistaStatsDTO stats = NutricionistaStatsDTO.builder()
                .totalPlans(totalPlans)
                .activePlans(activePlans)
                .expiredPlans(expiredPlans)
                .averagePlanDuration(averageDuration)
                .totalPatients(totalPatients)
                .activePatientsCount(activePatientsCount)
                .plansSoonToExpire(plansSoonToExpire)
                .build();
                
        log.debug("Generated statistics for nutritionist {}: {}", nutricionistaId, stats);
        return stats;
    }

    // ================================
    // PATIENT-BASED OPERATIONS
    // ================================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-by-paciente", key = "#pacienteId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize",
              condition = "#pacienteId != null && #pacienteId > 0",
              unless = "#result.isEmpty()")
    public Page<PlanDietaDTO> findByPacienteId(Integer pacienteId, Pageable pageable) {
        log.debug("Finding diet plans for patient: {} with pagination", pacienteId);
        
        Page<PlanDieta> entityPage = repo.findByPacienteId(pacienteId, pageable);
        return entityPage.map(entity -> {
            PlanDietaDTO dto = mapper.toDTO(entity);
            if (entity.getFiltrosaplicado() != null) {
                dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
            }
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-by-paciente", key = "#pacienteId + '_all'",
              condition = "#pacienteId != null && #pacienteId > 0",
              unless = "#result.isEmpty()")
    public List<PlanDietaDTO> findByPacienteId(Integer pacienteId) {
        log.debug("Finding all diet plans for patient: {}", pacienteId);
        
        if (pacienteId == null || pacienteId <= 0) {
            log.warn("Invalid patient ID: {}", pacienteId);
            return List.of(); // Return empty list for invalid ID
        }
        
        // Verify patient exists
        if (!pacienteRepo.existsById(pacienteId)) {
            log.warn("Patient not found with ID: {}", pacienteId);
            return List.of(); // Return empty list for non-existent patient
        }
        
        List<PlanDieta> entities = repo.findByPacienteId(pacienteId);
        log.debug("Found {} diet plans for patient: {}", entities.size(), pacienteId);
        
        return entities.stream()
                .map(entity -> {
                    PlanDietaDTO dto = mapper.toDTO(entity);
                    if (entity.getFiltrosaplicado() != null) {
                        dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-current-active", key = "#pacienteId + '_' + #currentDate",
              condition = "#pacienteId != null && #pacienteId > 0")
    public Optional<PlanDietaDTO> findCurrentActivePlan(Integer pacienteId, LocalDate currentDate) {
        log.debug("Finding current active plan for patient: {} on date: {}", pacienteId, currentDate);
        
        if (pacienteId == null || pacienteId <= 0) {
            log.warn("Invalid patient ID: {}", pacienteId);
            return Optional.empty();
        }
        
        // Verify patient exists
        if (!pacienteRepo.existsById(pacienteId)) {
            log.warn("Patient not found with ID: {}", pacienteId);
            return Optional.empty();
        }
        
        LocalDate searchDate = currentDate != null ? currentDate : LocalDate.now();
        
        try {
            return repo.findCurrentActivePlanByPaciente(pacienteId, searchDate)
                    .map(entity -> {
                        PlanDietaDTO dto = mapper.toDTO(entity);
                        if (entity.getFiltrosaplicado() != null) {
                            dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
                        }
                        log.debug("Found active plan ID: {} for patient: {} on date: {}", 
                                 entity.getId(), pacienteId, searchDate);
                        return dto;
                    });
        } catch (Exception e) {
            log.error("Error finding current active plan for patient {}: {}", pacienteId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanDietaDTO> findCurrentActivePlan(Integer pacienteId) {
        return findCurrentActivePlan(pacienteId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-latest", key = "#pacienteId")
    public Optional<PlanDietaDTO> findLatestByPacienteId(Integer pacienteId) {
        log.debug("Finding latest plan for patient: {}", pacienteId);
        
        return repo.findLatestByPacienteId(pacienteId)
                .map(entity -> {
                    PlanDietaDTO dto = mapper.toDTO(entity);
                    if (entity.getFiltrosaplicado() != null) {
                        dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
                    }
                    return dto;
                });
    }

    // ================================
    // DATE-BASED OPERATIONS
    // ================================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-date-range", key = "#startDate + '_' + #endDate + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PlanDietaDTO> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("Finding diet plans in date range: {} to {} with pagination", startDate, endDate);
        
        Page<PlanDieta> entityPage = repo.findByDateRange(startDate, endDate, pageable);
        return entityPage.map(entity -> {
            PlanDietaDTO dto = mapper.toDTO(entity);
            if (entity.getFiltrosaplicado() != null) {
                dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
            }
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-expiring", key = "#daysAhead + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PlanDietaDTO> findExpiringSoon(Integer daysAhead, Pageable pageable) {
        log.debug("Finding plans expiring in {} days with pagination", daysAhead);
        
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysAhead);
        
        Page<PlanDieta> entityPage = repo.findExpiringSoon(today, endDate, pageable);
        return entityPage.map(entity -> {
            PlanDietaDTO dto = mapper.toDTO(entity);
            if (entity.getFiltrosaplicado() != null) {
                dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
            }
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "plandieta-expired", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PlanDietaDTO> findExpiredPlans(Pageable pageable) {
        log.debug("Finding expired plans with pagination");
        
        LocalDate today = LocalDate.now();
        
        Page<PlanDieta> entityPage = repo.findExpiredPlans(today, pageable);
        return entityPage.map(entity -> {
            PlanDietaDTO dto = mapper.toDTO(entity);
            if (entity.getFiltrosaplicado() != null) {
                dto.setFiltroAplicado(filtroMapper.toDTO(entity.getFiltrosaplicado()));
            }
            return dto;
        });
    }

    // ================================
    // BUSINESS LOGIC OPERATIONS
    // ================================

    @Override
    @Transactional(readOnly = true)
    public boolean validatePlanDates(Integer pacienteId, LocalDate startDate, LocalDate endDate, Integer excludePlanId) {
        log.debug("Validating plan dates for patient: {} from {} to {}", pacienteId, startDate, endDate);
        
        // Check for overlapping plans
        boolean hasOverlaps = repo.hasOverlappingPlans(pacienteId, startDate, endDate, excludePlanId);
        
        if (hasOverlaps) {
            log.warn("Overlapping plans found for patient: {} in date range {} to {}", pacienteId, startDate, endDate);
            return false;
        }
        
        // Additional business rules
        if (startDate.isAfter(endDate)) {
            log.warn("Invalid date range: start date {} is after end date {}", startDate, endDate);
            return false;
        }
        
        // Plan should not be more than 1 year in duration
        if (startDate.plusDays(365).isBefore(endDate)) {
            log.warn("Plan duration exceeds maximum allowed (1 year)");
            return false;
        }
        
        return true;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"plandieta", "plandieta-stats"}, allEntries = true)
    public int extendActivePlans(Integer nutricionistaId, Integer daysToExtend) {
        log.info("Extending active plans for nutritionist: {} by {} days", nutricionistaId, daysToExtend);
        
        LocalDate today = LocalDate.now();
        return repo.extendActivePlans(nutricionistaId, daysToExtend, today);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "shopping-list", key = "#pacienteId + '_' + #date")
    public List<Ingrediente> generateShoppingList(Integer pacienteId, LocalDate date) {
        log.debug("Generating shopping list for patient: {} on date: {}", pacienteId, date);
        
        LocalDate searchDate = date != null ? date : LocalDate.now();
        
        // Find current active plan
        Optional<PlanDieta> currentPlan = repo.findCurrentActivePlanByPaciente(pacienteId, searchDate);
        
        if (currentPlan.isEmpty()) {
            log.warn("No active plan found for patient: {} on date: {}", pacienteId, searchDate);
            return List.of();
        }
        
        // For now, return an empty list since the shopping list generation method is not available
        // This should be implemented based on the plan's meals and ingredients
        log.warn("Shopping list generation not yet fully implemented");
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingrediente> generateShoppingList(Integer pacienteId) {
        return generateShoppingList(pacienteId, LocalDate.now());
    }

    // ================================
    // PRIVATE HELPER METHODS
    // ================================

    private void validatePlanDietaCreation(PlanDietaDTO dto) {
        // Validate required fields
        if (dto.getIdNutricionista() == null) {
            throw new BusinessException("Nutritionist ID is required");
        }
        
        if (dto.getIdPaciente() == null) {
            throw new BusinessException("Patient ID is required");
        }
        
        // Validate nutritionist exists
        if (!nutricionistaRepo.existsById(dto.getIdNutricionista())) {
            throw new ResourceNotFoundException("Nutritionist not found with ID: " + dto.getIdNutricionista());
        }
        
        // Validate patient exists
        if (!pacienteRepo.existsById(dto.getIdPaciente())) {
            throw new ResourceNotFoundException("Patient not found with ID: " + dto.getIdPaciente());
        }
        
        // Validate dates if provided
        if (dto.getFechaini() != null && dto.getFechafin() != null) {
            if (!validatePlanDates(dto.getIdPaciente(), dto.getFechaini(), dto.getFechafin(), null)) {
                throw new BusinessException("Invalid plan dates or overlapping with existing plans");
            }
        }
    }

    private void validatePlanDietaUpdate(PlanDietaDTO dto, PlanDieta existingEntity) {
        // Basic validation
        validatePlanDietaCreation(dto);
        
        // Validate dates if changed
        if (dto.getFechaini() != null && dto.getFechafin() != null) {
            if (!Objects.equals(dto.getFechaini(), existingEntity.getFechaini()) ||
                !Objects.equals(dto.getFechafin(), existingEntity.getFechafin())) {
                
                if (!validatePlanDates(dto.getIdPaciente(), dto.getFechaini(), dto.getFechafin(), dto.getId())) {
                    throw new BusinessException("Invalid plan dates or overlapping with existing plans");
                }
            }
        }
    }
}
