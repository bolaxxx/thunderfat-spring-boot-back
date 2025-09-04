package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.BusinessException;
import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.ComidaRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dao.PlanDietaRepository;
import com.thunderfat.springboot.backend.model.dao.PlatoPlanDietaRepository;
import com.thunderfat.springboot.backend.model.dao.PlatoPredeterminadoRepository;
import com.thunderfat.springboot.backend.model.dto.ComidaDTO;
import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.ComidaMapper;
import com.thunderfat.springboot.backend.model.dto.mapper.PlatoPredeterminadoMapper;
import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Modern Spring Boot 2025 implementation of Comida (Meal) service.
 * Provides comprehensive meal management functionality for nutrition plans.
 * 
 * Key Features:
 * - Complete CRUD operations with modern DTO patterns
 * - Advanced caching for performance optimization
 * - Method-level security for healthcare data protection
 * - Meal analytics and nutritional analysis
 * - Intelligent meal substitution recommendations
 * - Backward compatibility with legacy methods
 * - Comprehensive logging and monitoring
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ComidaServiceJPA implements IComidaService {
    
    // Direct repository injection (no service dependencies)
    private final ComidaRepository comidaRepository;
    private final PacienteRepository pacienteRepository;
    private final PlanDietaRepository planDietaRepository;
    private final PlatoPlanDietaRepository platoPlanDietaRepository;
    private final PlatoPredeterminadoRepository platoPredeterminadoRepository;
    
    // MapStruct mappers
    private final ComidaMapper comidaMapper;
    private final PlatoPredeterminadoMapper platoPredeterminadoMapper;
    
    // ================================
    // LEGACY CRUD OPERATIONS (DEPRECATED)
    // ================================
    
    @Override
    @Transactional
    @Deprecated
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public void insertar(Comida comida) {
        log.warn("Using deprecated insertar method. Consider using save(ComidaDTO) instead.");
        try {
            comidaRepository.save(comida);
            log.debug("Legacy comida inserted with ID: {}", comida.getId());
        } catch (Exception e) {
            log.error("Error inserting legacy comida", e);
            throw new BusinessException("Error al insertar comida", e);
        }
    }
    
    @Override
    @Transactional
    @Deprecated
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public void eliminar(int id_comida) {
        log.warn("Using deprecated eliminar method. Consider using deleteById(Integer) instead.");
        try {
            comidaRepository.deleteById(id_comida);
            log.debug("Legacy comida deleted with ID: {}", id_comida);
        } catch (Exception e) {
            log.error("Error deleting legacy comida with ID: {}", id_comida, e);
            throw new BusinessException("Error al eliminar comida", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<Comida> listaPorPlanDieta(PlanDieta planDieta) {
        log.warn("Using deprecated listaPorPlanDieta method. Consider using findByPlanDietaId instead.");
        try {
            return comidaRepository.findByPlandieta(planDieta.getId());
        } catch (Exception e) {
            log.error("Error finding comidas by plan dieta", e);
            throw new BusinessException("Error al buscar comidas por plan de dieta", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public Comida buscarPorID(int id_comida) {
        log.warn("Using deprecated buscarPorID method. Consider using findById(Integer) instead.");
        try {
            return comidaRepository.findById(id_comida).orElse(null);
        } catch (Exception e) {
            log.error("Error finding legacy comida by ID: {}", id_comida, e);
            throw new BusinessException("Error al buscar comida", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Deprecated
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#id_paciente, authentication)")
    public List<PlatoPredeterminado> bucarcambios(int id_paciente, int id_plato) {
        log.warn("Using deprecated bucarcambios method. Consider using findMealSubstitutions instead.");
        try {
            // Legacy implementation preserved for backward compatibility
            return findMealSubstitutionsInternal(id_paciente, id_plato);
        } catch (Exception e) {
            log.error("Error finding legacy meal substitutions for patient: {} and plate: {}", id_paciente, id_plato, e);
            throw new BusinessException("Error al buscar cambios de comida", e);
        }
    }
    
    // ================================
    // MODERN DTO-BASED OPERATIONS
    // ================================
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ComidaDTO> findAll(Pageable pageable) {
        log.debug("Finding all comidas with pagination: page {}, size {}", 
                  pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Comida> comidasPage = comidaRepository.findAll(pageable);
            return comidasPage.map(comidaMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding all comidas with pagination", e);
            throw new BusinessException("Error al listar comidas", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#id")
    @PostAuthorize("@securityService.canViewComida(returnObject, authentication)")
    public Optional<ComidaDTO> findById(Integer id) {
        log.debug("Finding comida by ID: {}", id);
        
        try {
            return comidaRepository.findById(id)
                    .map(comidaMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding comida by ID: {}", id, e);
            throw new BusinessException("Error al buscar comida", e);
        }
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"comidas", "comida-stats"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public ComidaDTO save(ComidaDTO comidaDTO) {
        log.info("Saving comida: {}", comidaDTO);
        
        try {
            Comida comida = comidaMapper.toEntity(comidaDTO);
            Comida savedComida = comidaRepository.save(comida);
            
            ComidaDTO result = comidaMapper.toDto(savedComida);
            log.debug("Comida saved successfully with ID: {}", result.getId());
            
            return result;
        } catch (Exception e) {
            log.error("Error saving comida: {}", comidaDTO, e);
            throw new BusinessException("Error al guardar comida", e);
        }
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"comidas", "comida-stats"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public void deleteById(Integer id) {
        log.info("Deleting comida with ID: {}", id);
        
        try {
            if (!comidaRepository.existsById(id)) {
                throw new ResourceNotFoundException("Comida not found with ID: " + id);
            }
            
            comidaRepository.deleteById(id);
            log.debug("Comida deleted successfully with ID: {}", id);
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting comida with ID: {}", id, e);
            throw new BusinessException("Error al eliminar comida", e);
        }
    }
    
    // ================================
    // BUSINESS-SPECIFIC OPERATIONS
    // ================================
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#planDietaId + '-' + #pageable.pageNumber")
    @PreAuthorize("@securityService.canAccessPlanDieta(#planDietaId, authentication)")
    public Page<ComidaDTO> findByPlanDietaId(Integer planDietaId, Pageable pageable) {
        log.debug("Finding comidas by plan dieta ID: {} with pagination", planDietaId);
        
        try {
            Page<Comida> comidasPage = comidaRepository.findByPlanDietaId(planDietaId, pageable);
            return comidasPage.map(comidaMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding comidas by plan dieta ID: {}", planDietaId, e);
            throw new BusinessException("Error al buscar comidas por plan de dieta", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#nutricionistaId + '-nutritionist-' + #pageable.pageNumber")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.isNutricionistaOwner(#nutricionistaId, authentication) or hasRole('ADMIN')")
    public Page<ComidaDTO> findByNutricionistaId(Integer nutricionistaId, Pageable pageable) {
        log.debug("Finding comidas by nutritionist ID: {} with pagination", nutricionistaId);
        
        try {
            Page<Comida> comidasPage = comidaRepository.findByNutricionistaId(nutricionistaId, pageable);
            return comidasPage.map(comidaMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding comidas by nutritionist ID: {}", nutricionistaId, e);
            throw new BusinessException("Error al buscar comidas por nutricionista", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#pacienteId + '-today-' + #date")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#pacienteId, authentication)")
    public List<ComidaDTO> findTodayMeals(Integer pacienteId, LocalDate date) {
        log.debug("Finding today's meals for patient: {} on date: {}", pacienteId, date);
        
        try {
            List<Comida> comidas = comidaRepository.findTodayMeals(pacienteId, date);
            return comidas.stream()
                    .map(comidaMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding today's meals for patient: {} on date: {}", pacienteId, date, e);
            throw new BusinessException("Error al buscar comidas del día", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comida-substitutions", key = "#pacienteId + '-' + #platoId")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#pacienteId, authentication)")
    public List<PlatoPredeterminadoDTO> findMealSubstitutions(Integer pacienteId, Integer platoId) {
        log.debug("Finding meal substitutions for patient: {} and plate: {}", pacienteId, platoId);
        
        try {
            List<PlatoPredeterminado> substitutions = findMealSubstitutionsInternal(pacienteId, platoId);
            return substitutions.stream()
                    .map(platoPredeterminadoMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding meal substitutions for patient: {} and plate: {}", pacienteId, platoId, e);
            throw new BusinessException("Error al buscar sustituciones de comida", e);
        }
    }
    
    // ================================
    // ANALYTICS & REPORTING
    // ================================
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comida-stats", key = "#nutricionistaId + '-count'")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.isNutricionistaOwner(#nutricionistaId, authentication) or hasRole('ADMIN')")
    public Long countMealsByNutritionist(Integer nutricionistaId) {
        log.debug("Counting meals by nutritionist: {}", nutricionistaId);
        
        try {
            return comidaRepository.countByNutricionistaId(nutricionistaId);
        } catch (Exception e) {
            log.error("Error counting meals by nutritionist: {}", nutricionistaId, e);
            throw new BusinessException("Error al contar comidas", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comida-stats", key = "#nutricionistaId + '-active-count'")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.isNutricionistaOwner(#nutricionistaId, authentication) or hasRole('ADMIN')")
    public Long countActiveMealsByNutritionist(Integer nutricionistaId) {
        log.debug("Counting active meals by nutritionist: {}", nutricionistaId);
        
        try {
            return comidaRepository.countActiveMealsByNutricionistaId(nutricionistaId);
        } catch (Exception e) {
            log.error("Error counting active meals by nutritionist: {}", nutricionistaId, e);
            throw new BusinessException("Error al contar comidas activas", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comida-stats", key = "#nutricionistaId + '-avg-calories'")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.isNutricionistaOwner(#nutricionistaId, authentication) or hasRole('ADMIN')")
    public Double getAverageMealCalories(Integer nutricionistaId) {
        log.debug("Calculating average meal calories for nutritionist: {}", nutricionistaId);
        
        try {
            return comidaRepository.getAverageMealCaloriesByNutritionist(nutricionistaId);
        } catch (Exception e) {
            log.error("Error calculating average meal calories for nutritionist: {}", nutricionistaId, e);
            throw new BusinessException("Error al calcular promedio de calorías", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#nutricionistaId + '-popular-' + #pageable.pageNumber")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.isNutricionistaOwner(#nutricionistaId, authentication) or hasRole('ADMIN')")
    public Page<ComidaDTO> findMostPopularMeals(Integer nutricionistaId, Pageable pageable) {
        log.debug("Finding most popular meals for nutritionist: {} with pagination", nutricionistaId);
        
        try {
            Page<Comida> comidasPage = comidaRepository.findMostPopularMealsByNutritionist(nutricionistaId, pageable);
            return comidasPage.map(comidaMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding most popular meals for nutritionist: {}", nutricionistaId, e);
            throw new BusinessException("Error al buscar comidas populares", e);
        }
    }
    
    // ================================
    // ADDITIONAL BUSINESS OPERATIONS
    // ================================
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#pacienteId + '-timerange-' + #horaInicio + '-' + #horaFin")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#pacienteId, authentication)")
    public List<ComidaDTO> findByTimeRange(Integer pacienteId, LocalTime horaInicio, LocalTime horaFin) {
        log.debug("Finding meals by time range for patient: {} between {} and {}", 
                 pacienteId, horaInicio, horaFin);
        
        try {
            List<Comida> comidas = comidaRepository.findByTimeRange(pacienteId, horaInicio, horaFin);
            return comidas.stream()
                    .map(comidaMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding meals by time range for patient: {}", pacienteId, e);
            throw new BusinessException("Error al buscar comidas por rango horario", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#nutricionistaId + '-high-rated-' + #pageable.pageNumber")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.isNutricionistaOwner(#nutricionistaId, authentication) or hasRole('ADMIN')")
    public Page<ComidaDTO> findHighRatedMealsByNutritionist(Integer nutricionistaId, Pageable pageable) {
        log.debug("Finding high rated meals for nutritionist: {} with pagination", nutricionistaId);
        
        try {
            Page<Comida> comidasPage = comidaRepository.findHighRatedMealsByNutritionist(nutricionistaId, pageable);
            return comidasPage.map(comidaMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding high rated meals for nutritionist: {}", nutricionistaId, e);
            throw new BusinessException("Error al buscar comidas mejor valoradas", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#nutricionistaId + '-low-rated'")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.isNutricionistaOwner(#nutricionistaId, authentication) or hasRole('ADMIN')")
    public List<ComidaDTO> findLowRatedMealsByNutritionist(Integer nutricionistaId) {
        log.debug("Finding low rated meals for nutritionist: {}", nutricionistaId);
        
        try {
            List<Comida> comidas = comidaRepository.findLowRatedMealsByNutritionist(nutricionistaId);
            return comidas.stream()
                    .map(comidaMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding low rated meals for nutritionist: {}", nutricionistaId, e);
            throw new BusinessException("Error al buscar comidas peor valoradas", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "comidas", key = "#nutricionistaId + '-date-range-' + #fechaInicio + '-' + #fechaFin + '-' + #pageable.pageNumber")
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.isNutricionistaOwner(#nutricionistaId, authentication) or hasRole('ADMIN')")
    public Page<ComidaDTO> findByDateRangeAndNutritionist(Integer nutricionistaId, LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        log.debug("Finding meals by date range for nutritionist: {} between {} and {}", 
                 nutricionistaId, fechaInicio, fechaFin);
        
        try {
            Page<Comida> comidasPage = comidaRepository.findByDateRangeAndNutritionist(
                nutricionistaId, fechaInicio, fechaFin, pageable);
            return comidasPage.map(comidaMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding meals by date range for nutritionist: {}", nutricionistaId, e);
            throw new BusinessException("Error al buscar comidas por rango de fechas", e);
        }
    }
    
    // ================================
    // PRIVATE HELPER METHODS
    // ================================
    
    /**
     * Internal method for meal substitution logic (preserved from legacy implementation)
     */
    private List<PlatoPredeterminado> findMealSubstitutionsInternal(Integer pacienteId, Integer platoId) {
        log.debug("Finding meal substitutions internally for patient: {} and plate: {}", pacienteId, platoId);
        
        try {
            // Get patient and current plan
            Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
            if (pacienteOpt.isEmpty()) {
                throw new ResourceNotFoundException("Patient not found with ID: " + pacienteId);
            }
            
            Paciente paciente = pacienteOpt.get();
            
            // Get current diet plan
            Optional<PlanDieta> planDietaOpt = planDietaRepository
                    .findCurrentActivePlanByPaciente(pacienteId, LocalDate.now());
            if (planDietaOpt.isEmpty()) {
                log.warn("No current diet plan found for patient: {}", pacienteId);
                return List.of();
            }
            
            PlanDieta planDieta = planDietaOpt.get();
            
            // Get current plate
            Optional<PlatoPlanDieta> platoOpt = platoPlanDietaRepository.findById(platoId);
            if (platoOpt.isEmpty()) {
                throw new ResourceNotFoundException("Plate not found with ID: " + platoId);
            }
            
            PlatoPlanDieta plato = platoOpt.get();
            
            // Get possible substitutions
            List<PlatoPredeterminado> possibleSubstitutions = 
                    platoPredeterminadoRepository.listapornutricionista(paciente.getNutricionista().getId());
            
            // Filter suitable substitutions
            return possibleSubstitutions.stream()
                    .filter(p -> !isIngredientInPlan(p, planDieta))
                    .filter(p -> isSustituible(plato, p))
                    .collect(Collectors.toList());
                    
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in findMealSubstitutionsInternal", e);
            throw new BusinessException("Error interno al buscar sustituciones", e);
        }
    }
    
    /**
     * Check if ingredient is in the current diet plan (legacy logic preserved)
     */
    private boolean isIngredientInPlan(PlatoPredeterminado plato, PlanDieta plandieta) {
        try {
            return plato.getIngredientes().stream()
                    .anyMatch(ingrediente -> 
                        plandieta.getFiltrosaplicado().getAlimentos().contains(ingrediente.getAlimento()));
        } catch (Exception e) {
            log.error("Error checking if ingredient is in plan", e);
            return false; // Conservative approach - don't exclude if error
        }
    }
    
    /**
     * Check if substitution is suitable based on calories (legacy logic preserved)
     */
    private boolean isSustituible(PlatoPlanDieta marcado, PlatoPredeterminado posible) {
        try {
            double maximo = (marcado.getKcaltotales() * 110) / 100; // 10% tolerance above
            double minimo = (marcado.getKcaltotales() * 90) / 100;  // 10% tolerance below
            return posible.getKcaltotales() < maximo && posible.getKcaltotales() > minimo;
        } catch (Exception e) {
            log.error("Error checking substitution suitability", e);
            return false; // Conservative approach
        }
    }
}
