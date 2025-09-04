package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.PlatoPlanDietaRepository;
import com.thunderfat.springboot.backend.model.dto.PlatoPlanDietaDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.PlatoPlanDietaMapper;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for managing PlatoPlanDieta entities.
 * Follows Spring Boot 2025 best practices including:
 * - Constructor injection with RequiredArgsConstructor
 * - Comprehensive caching strategy
 * - Full transaction management
 * - Complete validation and error handling
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class PlatoPlanDietaJPA implements IPlatoPlanDietaService {
    
    private final PlatoPlanDietaRepository platoPlanDietaRepository;
    
    // =====================================
    // MODERN METHODS (Spring Boot 2025)
    // =====================================
    
    /**
     * Find a PlatoPlanDieta by its ID
     * 
     * @param id The ID of the PlatoPlanDieta to find
     * @return An Optional containing the found PlatoPlanDietaDTO, or empty if not found
     */
    @Cacheable(value = "platos-plan-dieta", key = "#id")
    @Transactional(readOnly = true)
    public Optional<PlatoPlanDietaDTO> findById(@NotNull(message = "ID cannot be null") Integer id) {
        log.debug("Finding PlatoPlanDieta with ID: {}", id);
        
        return platoPlanDietaRepository.findById(id)
                .map(PlatoPlanDietaMapper.INSTANCE::toDto);
    }
    
    /**
     * Find all PlatoPlanDieta entries with pagination
     * 
     * @param pageable Pagination information
     * @return A page of PlatoPlanDietaDTO objects
     */
    @Cacheable(value = "platos-plan-dieta", key = "'all:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<PlatoPlanDietaDTO> findAll(Pageable pageable) {
        log.debug("Finding all PlatoPlanDieta with pagination: page={}, size={}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        return platoPlanDietaRepository.findAll(pageable)
                .map(PlatoPlanDietaMapper.INSTANCE::toDto);
    }
    
    /**
     * Find PlatoPlanDieta entries by ComidaId with pagination
     * 
     * @param comidaId The ID of the Comida to search by
     * @param pageable Pagination information
     * @return A page of PlatoPlanDietaDTO objects
     */
    @Cacheable(value = "platos-by-comida", key = "#comidaId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<PlatoPlanDietaDTO> findByComidaId(@NotNull(message = "Comida ID cannot be null") Integer comidaId, Pageable pageable) {
        log.debug("Finding PlatoPlanDieta for comida ID: {}", comidaId);
        
        return platoPlanDietaRepository.findByComidaId(comidaId, pageable)
                .map(PlatoPlanDietaMapper.INSTANCE::toDto);
    }
    
    /**
     * Create a new PlatoPlanDieta
     * 
     * @param platoPlanDietaDTO The DTO containing the PlatoPlanDieta data
     * @return The created PlatoPlanDietaDTO with generated ID
     */
    @Caching(evict = {
        @CacheEvict(value = "platos-plan-dieta", allEntries = true),
        @CacheEvict(value = "platos-by-comida", allEntries = true)
    })
    @Transactional
    public PlatoPlanDietaDTO create(@Valid @NotNull(message = "PlatoPlanDieta data cannot be null") PlatoPlanDietaDTO platoPlanDietaDTO) {
        log.info("Creating new PlatoPlanDieta");
        
        // Convert DTO to entity
        PlatoPlanDieta platoPlanDieta = PlatoPlanDietaMapper.INSTANCE.toEntity(platoPlanDietaDTO);
        
        // Save entity
        PlatoPlanDieta savedPlatoPlanDieta = platoPlanDietaRepository.save(platoPlanDieta);
        
        // Convert back to DTO
        PlatoPlanDietaDTO result = PlatoPlanDietaMapper.INSTANCE.toDto(savedPlatoPlanDieta);
        log.info("Successfully created PlatoPlanDieta with ID: {}", result.getId());
        
        return result;
    }
    
    /**
     * Update an existing PlatoPlanDieta
     * 
     * @param id The ID of the PlatoPlanDieta to update
     * @param platoPlanDietaDTO The DTO containing the updated data
     * @return The updated PlatoPlanDietaDTO
     * @throws ResourceNotFoundException if the PlatoPlanDieta is not found
     */
    @Caching(evict = {
        @CacheEvict(value = "platos-plan-dieta", key = "#id"),
        @CacheEvict(value = "platos-by-comida", allEntries = true)
    })
    @Transactional
    public PlatoPlanDietaDTO update(
            @NotNull(message = "ID cannot be null") Integer id, 
            @Valid @NotNull(message = "PlatoPlanDieta data cannot be null") PlatoPlanDietaDTO platoPlanDietaDTO) {
        log.info("Updating PlatoPlanDieta with ID: {}", id);
        
        // Retrieve existing entity
        PlatoPlanDieta existingEntity = platoPlanDietaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlatoPlanDieta not found with id: " + id));
        
        // Ensure ID is set
        platoPlanDietaDTO.setId(id);
        
        // Update entity from DTO using the efficient mapper method
        PlatoPlanDietaMapper.INSTANCE.updateEntityFromDto(platoPlanDietaDTO, existingEntity);
        
        // Save entity
        PlatoPlanDieta savedPlatoPlanDieta = platoPlanDietaRepository.save(existingEntity);
        
        // Convert back to DTO
        PlatoPlanDietaDTO result = PlatoPlanDietaMapper.INSTANCE.toDto(savedPlatoPlanDieta);
        log.info("Successfully updated PlatoPlanDieta with ID: {}", id);
        
        return result;
    }
    
    /**
     * Delete a PlatoPlanDieta by its ID
     * 
     * @param id The ID of the PlatoPlanDieta to delete
     * @throws ResourceNotFoundException if the PlatoPlanDieta is not found
     */
    @Caching(evict = {
        @CacheEvict(value = "platos-plan-dieta", key = "#id"),
        @CacheEvict(value = "platos-by-comida", allEntries = true)
    })
    @Transactional
    public void deleteById(@NotNull(message = "ID cannot be null") Integer id) {
        log.info("Deleting PlatoPlanDieta with ID: {}", id);
        
        // Check existence
        if (!platoPlanDietaRepository.existsById(id)) {
            throw new ResourceNotFoundException("PlatoPlanDieta not found with id: " + id);
        }
        
        // Delete
        platoPlanDietaRepository.deleteById(id);
        log.info("Successfully deleted PlatoPlanDieta with ID: {}", id);
    }
    
    // =====================================
    // LEGACY METHODS (Backward Compatibility)
    // =====================================
    
    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public PlatoPlanDieta buscarPorId(int id_plato) {
        log.warn("Using deprecated method buscarPorId(). Consider migrating to findById()");
        return platoPlanDietaRepository.findById(id_plato).orElse(null);
    }
    
    @Override
    @Deprecated
    @Transactional
    public void eliminar(int id_plato) {
        log.warn("Using deprecated method eliminar(). Consider migrating to deleteById()");
        deleteById(id_plato);
    }
    
    @Override
    @Deprecated
    @Transactional
    public void insertar(PlatoPlanDieta plato) {
        log.warn("Using deprecated method insertar(). Consider migrating to create()");
        
        if (plato == null) {
            throw new IllegalArgumentException("PlatoPlanDieta cannot be null");
        }
        
        // Convert to DTO, create, then discard result since void return
        PlatoPlanDietaDTO dto = PlatoPlanDietaMapper.INSTANCE.toDto(plato);
        create(dto);
    }

}
