package com.thunderfat.springboot.backend.model.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import com.thunderfat.springboot.backend.model.dto.PlatoPlanDietaDTO;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Service interface for PlatoPlanDieta management operations.
 * Includes both modern and legacy methods for backward compatibility.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Validated
public interface IPlatoPlanDietaService {
    // Legacy methods
    PlatoPlanDieta buscarPorId(int id_plato);
    void eliminar(int id_plato);
    void insertar(PlatoPlanDieta plato);
    
    // Modern methods - these should be the preferred API
    Optional<PlatoPlanDietaDTO> findById(@NotNull(message = "ID cannot be null") Integer id);
    Page<PlatoPlanDietaDTO> findAll(Pageable pageable);
    Page<PlatoPlanDietaDTO> findByComidaId(@NotNull Integer comidaId, Pageable pageable);
    PlatoPlanDietaDTO create(@Valid @NotNull PlatoPlanDietaDTO platoPlanDietaDTO);
    PlatoPlanDietaDTO update(@NotNull Integer id, @Valid @NotNull PlatoPlanDietaDTO platoPlanDietaDTO);
    void deleteById(@NotNull Integer id);
}
