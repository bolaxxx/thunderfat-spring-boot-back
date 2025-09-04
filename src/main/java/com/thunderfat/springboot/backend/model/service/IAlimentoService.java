package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;

/**
 * Service interface for Alimento operations
 * Updated with Spring Boot 2025 best practices - DTO-based operations
 */
public interface IAlimentoService {
    
    /**
     * Get all foods with pagination
     */
    Page<AlimentoDTO> listarAlimentos(Pageable pageable);
    
    /**
     * Search foods by name with pagination
     */
    Page<AlimentoDTO> buscarPorNombre(String nombre, Pageable pageable);
    
    /**
     * Get food by ID
     */
    AlimentoDTO buscarPorId(Integer id);
    
    /**
     * Create new food with business validation
     */
    AlimentoDTO crear(AlimentoDTO alimentoDTO);
    
    /**
     * Update existing food with business validation
     */
    AlimentoDTO actualizar(Integer id, AlimentoDTO alimentoDTO);
    
    /**
     * Partial update (only non-null fields)
     */
    AlimentoDTO actualizarParcial(Integer id, AlimentoDTO alimentoDTO);
    
    /**
     * Delete food by ID
     */
    void eliminar(Integer id);
    
    /**
     * Get foods by estado with pagination
     */
    Page<AlimentoDTO> buscarPorEstado(String estado, Pageable pageable);
    
    /**
     * Get foods by calorie range
     */
    Page<AlimentoDTO> buscarPorRangoCalorias(Double minCal, Double maxCal, Pageable pageable);
    
    /**
     * Get high-protein foods
     */
    List<AlimentoDTO> buscarAlimentosAltoProteina(Double threshold);
    
    /**
     * Get low-calorie foods
     */
    List<AlimentoDTO> buscarAlimentosBajaCaloria(Double threshold);
    
    /**
     * Get foods rich in specific vitamin
     */
    List<AlimentoDTO> buscarAlimentosRicosEnVitamina(String vitamin, Double threshold);
    
    /**
     * Get foods suitable for diet plans
     */
    Page<AlimentoDTO> buscarAlimentosParaDieta(Double minCal, Double maxCal, 
                                               Double minProtein, Double maxFat, 
                                               Pageable pageable);
    
    /**
     * Legacy method for backward compatibility - simple list for select dropdowns
     */
    List<AlimentoDTO> listarParaSelect();
}
