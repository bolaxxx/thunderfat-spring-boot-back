package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Service interface for PlatoPredeterminado (predetermined dishes) management.
 * Includes both modern and legacy methods for backward compatibility.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Validated
public interface IPlatoPredetereminadoService {
    
    // =====================================
    // LEGACY METHODS
    // =====================================
    
    /**
     * Find a predetermined dish by ID (legacy method)
     * @param id_plato The dish ID
     * @return The dish entity or null if not found
     */
    @Deprecated
    PlatoPredeterminado buscarPorId(int id_plato);
    
    /**
     * Delete a predetermined dish (legacy method)
     * @param id_plato The dish ID
     */
    @Deprecated
    void eliminarPlatoPredeterminado(int id_plato);
    
    /**
     * Create or update a predetermined dish (legacy method)
     * @param plato The dish entity
     * @param id_nutricionista The nutritionist ID
     * @return The saved dish entity
     */
    @Deprecated
    PlatoPredeterminado insertar(PlatoPredeterminado plato, int id_nutricionista);
    
    /**
     * List predetermined dishes by nutritionist (legacy method)
     * @param nutricionista The nutritionist ID
     * @return List of dish entities
     */
    @Deprecated
    List<PlatoPredeterminado> listarPorNutricionista(int nutricionista);
    
    // =====================================
    // MODERN METHODS
    // =====================================
    
    /**
     * Find a predetermined dish by ID
     * @param id The dish ID
     * @return Optional containing the dish DTO or empty if not found
     */
    Optional<PlatoPredeterminadoDTO> findById(@NotNull Integer id);
    
    /**
     * Find all predetermined dishes with pagination
     * @param pageable The pagination information
     * @return Page of dish DTOs
     */
    Page<PlatoPredeterminadoDTO> findAll(Pageable pageable);
    
    /**
     * Find predetermined dishes by nutritionist with pagination
     * @param nutricionistaId The nutritionist ID
     * @param pageable The pagination information
     * @return Page of dish DTOs
     */
    Page<PlatoPredeterminadoDTO> findByNutricionistaId(@NotNull Integer nutricionistaId, Pageable pageable);
    
    /**
     * Find all predetermined dishes by nutritionist without pagination
     * @param nutricionistaId The nutritionist ID
     * @return List of dish DTOs
     */
    List<PlatoPredeterminadoDTO> findAllByNutricionistaId(@NotNull Integer nutricionistaId);
    
    /**
     * Create a new predetermined dish
     * @param platoPredeterminadoDTO The dish DTO
     * @param nutricionistaId The nutritionist ID
     * @return The created dish DTO
     */
    PlatoPredeterminadoDTO create(@Valid @NotNull PlatoPredeterminadoDTO platoPredeterminadoDTO, @NotNull Integer nutricionistaId);
    
    /**
     * Delete a predetermined dish by ID
     * @param id The dish ID
     */
    void deleteById(@NotNull Integer id);
}
