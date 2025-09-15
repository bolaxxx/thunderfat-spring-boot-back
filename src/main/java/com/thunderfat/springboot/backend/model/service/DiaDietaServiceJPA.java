package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.DiaDietaRepository;
import com.thunderfat.springboot.backend.model.dao.PlanDietaRepository;
import com.thunderfat.springboot.backend.model.dto.DiaDietaDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.DiaDietaMapper;
import com.thunderfat.springboot.backend.model.entity.DiaDieta;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaDietaServiceJPA implements IDIaDietaService {

    private final DiaDietaRepository diaDietaRepository;
    private final PlanDietaRepository planDietaRepository;
    private final DiaDietaMapper diaDietaMapper;
    

    // ============= Métodos Legacy (Deprecados) =============
    
    /**
     * @deprecated Use {@link #save(DiaDietaDTO)} instead.
     */
    @Override
    @Deprecated
    @Transactional
    public DiaDieta insertar(DiaDieta dia) {
        log.debug("Método deprecado: insertar(DiaDieta)");
        diaDietaRepository.save(dia);
        return dia;
    }

    /**
     * @deprecated Use {@link #findById(int)} instead.
     */
    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public DiaDieta buscarporID(int id) {
        log.debug("Método deprecado: buscarporID({})", id);
        return diaDietaRepository.findById(id).orElse(null);
    }

    /**
     * @deprecated Use {@link #findByPlanId(int)} instead.
     */
    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<DiaDieta> buscarporPlan(int id_plan) {
        log.debug("Método deprecado: buscarporPlan({})", id_plan);
        // Implementación vacía - se reemplazará por findByPlanId
        return null;
    }

    /**
     * @deprecated Use {@link #deleteById(int)} instead.
     */
    @Override
    @Deprecated
    @Transactional
    public void eliminar(int id_dia) {
        log.debug("Método deprecado: eliminar({})", id_dia);
        diaDietaRepository.deleteById(id_dia);
    }

    // ============= Métodos Modernos =============
    
    /**
     * Encuentra todos los días de dieta.
     *
     * @return Lista de todos los días de dieta como DTOs
     */
    @Transactional(readOnly = true)
    @Cacheable("diaDietas")
    public List<DiaDietaDTO> findAll() {
        log.debug("Obteniendo todos los días de dieta");
        return diaDietaRepository.findAll().stream()
                .map(diaDietaMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Encuentra un día de dieta por su ID.
     *
     * @param id El ID del día de dieta
     * @return El DTO del día de dieta
     * @throws ResourceNotFoundException si el día de dieta no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "diaDieta", key = "#id")
    public DiaDietaDTO findById(int id) {
        log.debug("Buscando día de dieta con ID: {}", id);
        return diaDietaRepository.findById(id)
                .map(diaDietaMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Día de dieta no encontrado con ID: " + id));
    }
    
    /**
     * Guarda un nuevo día de dieta.
     *
     * @param diaDietaDTO El DTO del día de dieta a guardar
     * @return El DTO del día de dieta guardado
     */
    @Transactional
    @CacheEvict(value = {"diaDieta", "diaDietas", "planDietaDias"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public DiaDietaDTO save(DiaDietaDTO diaDietaDTO) {
        log.debug("Guardando día de dieta: {}", diaDietaDTO);
        DiaDieta diaDieta = diaDietaMapper.toEntity(diaDietaDTO);
        DiaDieta savedDiaDieta = diaDietaRepository.save(diaDieta);
        return diaDietaMapper.toDTO(savedDiaDieta);
    }
    
    /**
     * Guarda un nuevo día de dieta en un plan específico.
     *
     * @param diaDietaDTO El DTO del día de dieta a guardar
     * @param planId El ID del plan de dieta al que pertenece
     * @return El DTO del día de dieta guardado
     * @throws ResourceNotFoundException si el plan de dieta no existe
     */
    @Transactional
    @CacheEvict(value = {"diaDieta", "diaDietas", "planDietaDias"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.canEditPlanDieta(#planId, authentication.name)")
    public DiaDietaDTO saveForPlan(DiaDietaDTO diaDietaDTO, int planId) {
        log.debug("Guardando día de dieta para plan con ID: {}", planId);
        
        PlanDieta planDieta = planDietaRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de dieta no encontrado con ID: " + planId));
        
        DiaDieta diaDieta = diaDietaMapper.toEntity(diaDietaDTO);
        DiaDieta savedDiaDieta = diaDietaRepository.save(diaDieta);
        
        // Añadir el día al plan
        planDieta.getDias().add(savedDiaDieta);
        planDietaRepository.save(planDieta);
        
        return diaDietaMapper.toDTO(savedDiaDieta);
    }
    
    /**
     * Actualiza un día de dieta existente.
     *
     * @param id El ID del día de dieta a actualizar
     * @param diaDietaDTO Los nuevos datos del día de dieta
     * @return El DTO del día de dieta actualizado
     * @throws ResourceNotFoundException si el día de dieta no existe
     */
    @Transactional
    @CacheEvict(value = {"diaDieta", "diaDietas", "planDietaDias"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.canEditDiaDieta(#id, authentication.name)")
    public DiaDietaDTO update(int id, DiaDietaDTO diaDietaDTO) {
        log.debug("Actualizando día de dieta con ID: {}", id);
        
        if (!diaDietaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Día de dieta no encontrado con ID: " + id);
        }
        
        DiaDieta diaDieta = diaDietaMapper.toEntity(diaDietaDTO);
        diaDieta.setId(id);
        DiaDieta updatedDiaDieta = diaDietaRepository.save(diaDieta);
        
        return diaDietaMapper.toDTO(updatedDiaDieta);
    }
    
    /**
     * Elimina un día de dieta por su ID.
     *
     * @param id El ID del día de dieta a eliminar
     * @throws ResourceNotFoundException si el día de dieta no existe
     */
    @Transactional
    @CacheEvict(value = {"diaDieta", "diaDietas", "planDietaDias"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.canEditDiaDieta(#id, authentication.name)")
    public void deleteById(int id) {
        log.debug("Eliminando día de dieta con ID: {}", id);
        
        if (!diaDietaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Día de dieta no encontrado con ID: " + id);
        }
        
        diaDietaRepository.deleteById(id);
    }
    
    /**
     * Encuentra los días de dieta para un plan específico.
     *
     * @param planId El ID del plan de dieta
     * @return Lista de días de dieta como DTOs
     * @throws ResourceNotFoundException si el plan de dieta no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "planDietaDias", key = "#planId")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.canViewPlanDieta(#planId, authentication.name)")
    public List<DiaDietaDTO> findByPlanId(int planId) {
        log.debug("Buscando días de dieta para plan con ID: {}", planId);
        
        PlanDieta planDieta = planDietaRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de dieta no encontrado con ID: " + planId));
        
        return planDieta.getDias().stream()
                .map(diaDietaMapper::toDTO)
                .collect(Collectors.toList());
    }
}
