package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.BusinessException;
import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.AntecedenteClinicoRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dto.AntecedentesClinicosDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.AntecedentesClinicosMapper;
import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Modern Spring Boot 2025 implementation of AntecedenteClinico (Clinical History) service.
 * Provides comprehensive clinical history management functionality for patients.
 * 
 * Key Features:
 * - Complete CRUD operations with modern DTO patterns
 * - Advanced caching for performance optimization
 * - Method-level security for healthcare data protection
 * - Specialized medical condition queries
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
public class AntecedenteClinicoServiceJPA implements IAntecedenteClinicoService {
    
    // Direct repository injection with constructor injection
    private final AntecedenteClinicoRepository antecedentesRepo;
    private final PacienteRepository pacienteRepository;
    
    // MapStruct mapper
    private final AntecedentesClinicosMapper antecedentesClinicosMapper;
    
    // ...existing code...
    
    // ================================
    // MODERN DTO-BASED OPERATIONS
    // ================================
    
    /**
     * Find all clinical antecedents with pagination
     * 
     * @param pageable Pagination information
     * @return Page of clinical antecedent DTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedentes", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<AntecedentesClinicosDTO> findAll(Pageable pageable) {
        log.debug("Finding all antecedentes clinicos with pagination: page {}, size {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<AntecedentesClinicos> antecedentesPaged = antecedentesRepo.findAll(pageable);
            return antecedentesPaged.map(antecedentesClinicosMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding all antecedentes clinicos with pagination", e);
            throw new BusinessException("Error al listar antecedentes clínicos", e);
        }
    }
    
    /**
     * Find clinical antecedent by ID
     * 
     * @param id Clinical antecedent ID
     * @return Optional clinical antecedent DTO
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedentes", key = "#id")
    public Optional<AntecedentesClinicosDTO> findById(Integer id) {
        log.debug("Finding antecedente clinico by ID: {}", id);
        
        try {
            return antecedentesRepo.findById(id)
                    .map(antecedentesClinicosMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding antecedente clinico by ID: {}", id, e);
            throw new BusinessException("Error al buscar antecedente clínico", e);
        }
    }
    
    /**
     * Save or update a clinical antecedent
     * 
     * @param antecedenteDTO Clinical antecedent data
     * @return Saved clinical antecedent DTO
     */
    @Override
    @Transactional
    @CacheEvict(value = {"antecedentes", "antecedentes-stats"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public AntecedentesClinicosDTO save(AntecedentesClinicosDTO antecedenteDTO) {
        log.info("Saving antecedente clinico: {}", antecedenteDTO);
        
        try {
            // Validate the patient exists if ID is provided
            if (antecedenteDTO.getIdPaciente() > 0) {
                if (!pacienteRepository.existsById(antecedenteDTO.getIdPaciente())) {
                    throw new ResourceNotFoundException("Paciente not found with ID: " + antecedenteDTO.getIdPaciente());
                }
            }
            
            AntecedentesClinicos antecedente = antecedentesClinicosMapper.toEntity(antecedenteDTO);
            AntecedentesClinicos savedAntecedente = antecedentesRepo.save(antecedente);
            
            AntecedentesClinicosDTO result = antecedentesClinicosMapper.toDto(savedAntecedente);
            log.debug("Antecedente clinico saved successfully with ID: {}", result.getId());
            
            return result;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error saving antecedente clinico: {}", antecedenteDTO, e);
            throw new BusinessException("Error al guardar antecedente clínico", e);
        }
    }
    
    /**
     * Delete a clinical antecedent by ID
     * 
     * @param id Clinical antecedent ID
     */
    @Override
    @Transactional
    @CacheEvict(value = {"antecedentes", "antecedentes-stats"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    public void deleteById(Integer id) {
        log.info("Deleting antecedente clinico with ID: {}", id);
        
        try {
            if (!antecedentesRepo.existsById(id)) {
                throw new ResourceNotFoundException("Antecedente clinico not found with ID: " + id);
            }
            
            antecedentesRepo.deleteById(id);
            log.debug("Antecedente clinico deleted successfully with ID: {}", id);
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting antecedente clinico with ID: {}", id, e);
            throw new BusinessException("Error al eliminar antecedente clínico", e);
        }
    }
    
    // ================================
    // BUSINESS-SPECIFIC OPERATIONS
    // ================================
    
    /**
     * Find clinical antecedents by patient ID
     * 
     * @param pacienteId Patient ID
     * @return List of clinical antecedent DTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedentes", key = "'patient-' + #pacienteId")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#pacienteId, authentication)")
    public List<AntecedentesClinicosDTO> findByPacienteId(Integer pacienteId) {
        log.debug("Finding antecedentes clinicos by patient ID: {}", pacienteId);
        
        try {
            List<AntecedentesClinicos> antecedentes = antecedentesRepo.findByPacienteId(pacienteId);
            return antecedentes.stream()
                    .map(antecedentesClinicosMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding antecedentes clinicos by patient ID: {}", pacienteId, e);
            throw new BusinessException("Error al buscar antecedentes clínicos por paciente", e);
        }
    }
    
    /**
     * Find clinical antecedents by patient ID with pagination
     * 
     * @param pacienteId Patient ID
     * @param pageable Pagination information
     * @return Page of clinical antecedent DTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedentes", key = "'patient-' + #pacienteId + '-page-' + #pageable.pageNumber")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#pacienteId, authentication)")
    public Page<AntecedentesClinicosDTO> findByPacienteId(Integer pacienteId, Pageable pageable) {
        log.debug("Finding antecedentes clinicos by patient ID: {} with pagination", pacienteId);
        
        try {
            Page<AntecedentesClinicos> antecedentesPaged = antecedentesRepo.findByPacienteId(pacienteId, pageable);
            return antecedentesPaged.map(antecedentesClinicosMapper::toDto);
        } catch (Exception e) {
            log.error("Error finding antecedentes clinicos by patient ID: {} with pagination", pacienteId, e);
            throw new BusinessException("Error al buscar antecedentes clínicos por paciente", e);
        }
    }
    
    /**
     * Find allergies for a patient
     * 
     * @param pacienteId Patient ID
     * @return List of allergy-related clinical antecedent DTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedentes", key = "'allergies-' + #pacienteId")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#pacienteId, authentication)")
    public List<AntecedentesClinicosDTO> findAlergiasByPacienteId(Integer pacienteId) {
        log.debug("Finding allergies for patient ID: {}", pacienteId);
        
        try {
            List<AntecedentesClinicos> alergias = antecedentesRepo.findAlergiasByPacienteId(pacienteId);
            return alergias.stream()
                    .map(antecedentesClinicosMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding allergies for patient ID: {}", pacienteId, e);
            throw new BusinessException("Error al buscar alergias del paciente", e);
        }
    }
    
    /**
     * Find cardiovascular conditions for a patient
     * 
     * @param pacienteId Patient ID
     * @return List of cardiovascular-related clinical antecedent DTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedentes", key = "'cardiovascular-' + #pacienteId")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#pacienteId, authentication)")
    public List<AntecedentesClinicosDTO> findCondicionesCardiovascularByPacienteId(Integer pacienteId) {
        log.debug("Finding cardiovascular conditions for patient ID: {}", pacienteId);
        
        try {
            List<AntecedentesClinicos> condicionesCardiovascular = 
                    antecedentesRepo.findCondicionesCardiovascularByPacienteId(pacienteId);
            return condicionesCardiovascular.stream()
                    .map(antecedentesClinicosMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding cardiovascular conditions for patient ID: {}", pacienteId, e);
            throw new BusinessException("Error al buscar condiciones cardiovasculares del paciente", e);
        }
    }
    
    /**
     * Find diabetes-related conditions for a patient
     * 
     * @param pacienteId Patient ID
     * @return List of diabetes-related clinical antecedent DTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedentes", key = "'diabetes-' + #pacienteId")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#pacienteId, authentication)")
    public List<AntecedentesClinicosDTO> findCondicionesDiabeticasByPacienteId(Integer pacienteId) {
        log.debug("Finding diabetes conditions for patient ID: {}", pacienteId);
        
        try {
            List<AntecedentesClinicos> condicionesDiabeticas = 
                    antecedentesRepo.findCondicionesDiabeticasByPacienteId(pacienteId);
            return condicionesDiabeticas.stream()
                    .map(antecedentesClinicosMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding diabetes conditions for patient ID: {}", pacienteId, e);
            throw new BusinessException("Error al buscar condiciones diabéticas del paciente", e);
        }
    }
    
    /**
     * Find gastrointestinal conditions for a patient
     * 
     * @param pacienteId Patient ID
     * @return List of gastrointestinal-related clinical antecedent DTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedentes", key = "'gastrointestinal-' + #pacienteId")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteOwner(#pacienteId, authentication)")
    public List<AntecedentesClinicosDTO> findCondicionesGastrointestinalByPacienteId(Integer pacienteId) {
        log.debug("Finding gastrointestinal conditions for patient ID: {}", pacienteId);
        
        try {
            List<AntecedentesClinicos> condicionesGastrointestinal = 
                    antecedentesRepo.findCondicionesGastrointestinalByPacienteId(pacienteId);
            return condicionesGastrointestinal.stream()
                    .map(antecedentesClinicosMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding gastrointestinal conditions for patient ID: {}", pacienteId, e);
            throw new BusinessException("Error al buscar condiciones gastrointestinales del paciente", e);
        }
    }
    
    /**
     * Count clinical antecedents for a patient
     * 
     * @param pacienteId Patient ID
     * @return Count of clinical antecedents
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedentes-stats", key = "'count-' + #pacienteId")
    public Long countByPacienteId(Integer pacienteId) {
        log.debug("Counting antecedentes clinicos for patient ID: {}", pacienteId);
        
        try {
            return antecedentesRepo.countByPacienteId(pacienteId);
        } catch (Exception e) {
            log.error("Error counting antecedentes clinicos for patient ID: {}", pacienteId, e);
            throw new BusinessException("Error al contar antecedentes clínicos", e);
        }
    }
}
