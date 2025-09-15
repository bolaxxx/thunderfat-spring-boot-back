package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.PlatoPredeterminadoRepository;
import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.PlatoPredeterminadoMapper;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for managing PlatoPredeterminado (predetermined dishes) entities.
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
public class PlatoPredeterminadoJPA implements IPlatoPredetereminadoService {
    
    private final PlatoPredeterminadoRepository repo;
    private final INutricionistaService nutricionistaService; // For Nutricionista lookup

// =====================================
// MODERN METHODS (Spring Boot 2025)
// =====================================

/**
 * Find a PlatoPredeterminado by its ID and convert to DTO
 * 
 * @param id The ID of the predetermined dish to find
 * @return An Optional containing the found PlatoPredeterminadoDTO, or empty if not found
 */
@Cacheable(value = "platos-predeterminados", key = "#id", unless = "#result == null || #result.empty")
@Transactional(readOnly = true)
public Optional<PlatoPredeterminadoDTO> findById(@NotNull Integer id) {
    log.debug("Finding PlatoPredeterminado with ID: {}", id);
    
    return repo.findById(id)
            .map(PlatoPredeterminadoMapper.INSTANCE::toDto);
}

/**
 * Find all PlatoPredeterminado entries with pagination
 * 
 * @param pageable Pagination information
 * @return A page of PlatoPredeterminadoDTO objects
 */
@Cacheable(value = "platos-predeterminados", key = "'all:' + #pageable.pageNumber + ':' + #pageable.pageSize", 
          condition = "#pageable.pageSize <= 100", unless = "#result.isEmpty()")
@Transactional(readOnly = true)
public Page<PlatoPredeterminadoDTO> findAll(Pageable pageable) {
    log.debug("Finding all PlatoPredeterminado with pagination: page={}, size={}", 
             pageable.getPageNumber(), pageable.getPageSize());
    
    return repo.findAll(pageable)
            .map(PlatoPredeterminadoMapper.INSTANCE::toDto);
}

/**
 * Find PlatoPredeterminado entries by NutricionistaId with pagination
 * 
 * @param nutricionistaId The ID of the nutritionist to search by
 * @param pageable Pagination information
 * @return A page of PlatoPredeterminadoDTO objects
 */
@Cacheable(value = "platos-by-nutricionista", 
          key = "#nutricionistaId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize", 
          condition = "#nutricionistaId > 0", unless = "#result.isEmpty()")
@Transactional(readOnly = true)
public Page<PlatoPredeterminadoDTO> findByNutricionistaId(@NotNull Integer nutricionistaId, Pageable pageable) {
    log.debug("Finding PlatoPredeterminado for nutritionist ID: {}", nutricionistaId);
    
    // Create a PageRequest if not provided
    Pageable pageRequest = pageable != null ? pageable : Pageable.unpaged();
    
    // Use Spring Data JPA to create a custom pageable query using the Specification API
    Page<PlatoPredeterminado> results = repo.findAll(
        (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("nutricionista").get("id"), nutricionistaId),
        pageRequest
    );
    
    return results.map(PlatoPredeterminadoMapper.INSTANCE::toDto);
}

/**
 * Create a new PlatoPredeterminado with proper Nutricionista relationship
 * 
 * @param platoPredeterminadoDTO The DTO containing the PlatoPredeterminado data
 * @param nutricionistaId The ID of the nutritionist who creates this dish
 * @return The created PlatoPredeterminadoDTO with generated ID
 * @throws ResourceNotFoundException if the nutritionist doesn't exist
 */
@CacheEvict(cacheNames = {"platos-predeterminados", "platos-by-nutricionista", "platos-by-nutricionista-list"}, allEntries = true)
@Transactional
public PlatoPredeterminadoDTO create(
        @Valid @NotNull PlatoPredeterminadoDTO platoPredeterminadoDTO,
        @NotNull Integer nutricionistaId) {
    
    log.info("Creating new PlatoPredeterminado for nutritionist with ID: {}", nutricionistaId);
    
    // Find the nutritionist
    Nutricionista nutricionista = nutricionistaService.buscarPorId(nutricionistaId);
    if (nutricionista == null) {
        throw new ResourceNotFoundException("Nutritionist not found with ID: " + nutricionistaId);
    }
    
    // Convert DTO to entity
    PlatoPredeterminado platoPredeterminado = PlatoPredeterminadoMapper.INSTANCE.toEntity(platoPredeterminadoDTO);
    
    // Set the nutritionist
    platoPredeterminado.setNutricionista(nutricionista);
    
    // Save entity
    PlatoPredeterminado savedPlatoPredeterminado = repo.save(platoPredeterminado);
    
    // Convert back to DTO
    PlatoPredeterminadoDTO result = PlatoPredeterminadoMapper.INSTANCE.toDto(savedPlatoPredeterminado);
    log.info("Successfully created PlatoPredeterminado with ID: {}", result.getId());
    
    return result;
}

/**
 * Delete a PlatoPredeterminado by its ID
 * 
 * @param id The ID of the PlatoPredeterminado to delete
 * @throws ResourceNotFoundException if the PlatoPredeterminado is not found
 */
@CacheEvict(cacheNames = {"platos-predeterminados", "platos-by-nutricionista", "platos-by-nutricionista-list"}, allEntries = true)
@Transactional
public void deleteById(@NotNull Integer id) {
    log.info("Deleting PlatoPredeterminado with ID: {}", id);
    
    // Check existence
    if (!repo.existsById(id)) {
        throw new ResourceNotFoundException("PlatoPredeterminado not found with id: " + id);
    }
    
    // Delete
    repo.deleteById(id);
    log.info("Successfully deleted PlatoPredeterminado with ID: {}", id);
}

// =====================================
// LEGACY METHODS (Backward Compatibility)
// =====================================

@Override
@Deprecated
@Transactional(readOnly = true)
public PlatoPredeterminado buscarPorId(int id_plato) {
    log.warn("Using deprecated method buscarPorId(). Consider migrating to findById()");
    return repo.findById(id_plato).orElse(null);
}

@Override
@Deprecated
@Transactional
public void eliminarPlatoPredeterminado(int id_plato) {
    log.warn("Using deprecated method eliminarPlatoPredeterminado(). Consider migrating to deleteById()");
    deleteById(id_plato);
}

@Override
@Deprecated
@Transactional
public PlatoPredeterminado insertar(PlatoPredeterminado plato, int id_nutricionista) {
    log.warn("Using deprecated method insertar(). Consider migrating to create()");
    
    if (plato == null) {
        throw new IllegalArgumentException("PlatoPredeterminado cannot be null");
    }
    
    // Convert to DTO, create, then convert back to entity
    PlatoPredeterminadoDTO dto = PlatoPredeterminadoMapper.INSTANCE.toDto(plato);
    PlatoPredeterminadoDTO createdDto = create(dto, id_nutricionista);
    return PlatoPredeterminadoMapper.INSTANCE.toEntity(createdDto);
}

@Override
@Deprecated
@Transactional(readOnly = true)
public List<PlatoPredeterminado> listarPorNutricionista(int nutricionista) {
    log.warn("Using deprecated method listarPorNutricionista(). Consider migrating to findByNutricionistaId()");
    return repo.listapornutricionista(nutricionista);
}

/**
 * Find all predetermined dishes by nutritionist ID without pagination
 * @param nutricionistaId The nutritionist ID
 * @return List of DTOs
 */
@Cacheable(value = "platos-by-nutricionista-list", key = "#nutricionistaId", 
          condition = "#nutricionistaId > 0", unless = "#result.isEmpty()")
@Transactional(readOnly = true)
public List<PlatoPredeterminadoDTO> findAllByNutricionistaId(@NotNull Integer nutricionistaId) {
    log.debug("Finding all PlatoPredeterminado for nutritionist ID: {} (without pagination)", nutricionistaId);
    
    // Using our new Spring Data method naming convention
    List<PlatoPredeterminado> entities = repo.findByNutricionistaId(nutricionistaId);
    return PlatoPredeterminadoMapper.INSTANCE.toDtoList(entities);
}



}
