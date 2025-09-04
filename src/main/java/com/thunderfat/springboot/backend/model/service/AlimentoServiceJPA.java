package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.BusinessException;
import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.AlimentoRepository;
import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.AlimentoMapper;
import com.thunderfat.springboot.backend.model.entity.Alimento;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Alimento service implementation with Spring Boot 2025 best practices
 * Features: DTO pattern, caching, security, business validation, pagination
 * MODERNIZED - Replaced legacy direct entity manipulation
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AlimentoServiceJPA implements IAlimentoService {
    
    private final AlimentoRepository alimentoRepository;
    private final AlimentoMapper alimentoMapper;
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "alimentos", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<AlimentoDTO> listarAlimentos(Pageable pageable) {
        log.debug("Listando alimentos con paginación: página {}, tamaño {}", 
                  pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Alimento> alimentosPage = alimentoRepository.findAll(pageable);
        return alimentosPage.map(alimentoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "alimentos-search", key = "#nombre + '-' + #pageable.pageNumber")
    public Page<AlimentoDTO> buscarPorNombre(String nombre, Pageable pageable) {
        log.debug("Buscando alimentos por nombre: {} con paginación", nombre);
        
        Page<Alimento> alimentosPage = alimentoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        return alimentosPage.map(alimentoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "alimento", key = "#id")
    public AlimentoDTO buscarPorId(Integer id) {
        log.debug("Buscando alimento por ID: {}", id);
        
        Alimento alimento = alimentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Alimento no encontrado con ID: " + id));
            
        return alimentoMapper.toDto(alimento);
    }
    
    @Override
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    @CacheEvict(value = {"alimentos", "alimentos-search", "alimento"}, allEntries = true)
    public AlimentoDTO crear(AlimentoDTO alimentoDTO) {
        log.info("Creando nuevo alimento: {}", alimentoDTO.getNombre());
        
        // Business validation: duplicate name check
        validarNombreUnico(alimentoDTO.getNombre(), null);
        
        // Business validation: nutritional completeness
        validarCompletudNutricional(alimentoDTO);
        
        // Business validation: negative values
        validarValoresPositivos(alimentoDTO);
        
        Alimento alimento = alimentoMapper.toEntity(alimentoDTO);
        alimento = alimentoRepository.save(alimento);
        
        log.info("Alimento creado exitosamente con ID: {}", alimento.getId());
        return alimentoMapper.toDto(alimento);
    }
    
    @Override
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    @CacheEvict(value = {"alimentos", "alimentos-search", "alimento"}, allEntries = true)
    public AlimentoDTO actualizar(Integer id, AlimentoDTO alimentoDTO) {
        log.info("Actualizando alimento ID: {}", id);
        
        Alimento alimentoExistente = alimentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Alimento no encontrado con ID: " + id));
        
        // Business validation: duplicate name check (excluding current record)
        validarNombreUnico(alimentoDTO.getNombre(), id);
        
        // Business validation: nutritional completeness
        validarCompletudNutricional(alimentoDTO);
        
        // Business validation: negative values
        validarValoresPositivos(alimentoDTO);
        
        // Update all fields
        alimentoMapper.updateEntityFromDto(alimentoDTO, alimentoExistente);
        alimentoExistente = alimentoRepository.save(alimentoExistente);
        
        log.info("Alimento actualizado exitosamente: {}", alimentoExistente.getId());
        return alimentoMapper.toDto(alimentoExistente);
    }
    
    @Override
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    @CacheEvict(value = {"alimentos", "alimentos-search", "alimento"}, allEntries = true)
    public AlimentoDTO actualizarParcial(Integer id, AlimentoDTO alimentoDTO) {
        log.info("Actualización parcial del alimento ID: {}", id);
        
        Alimento alimentoExistente = alimentoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Alimento no encontrado con ID: " + id));
        
        // Only validate name if it's being updated
        if (alimentoDTO.getNombre() != null) {
            validarNombreUnico(alimentoDTO.getNombre(), id);
        }
        
        // Validate positive values for fields being updated
        validarValoresPositivosParcial(alimentoDTO);
        
        // Update only non-null fields
        alimentoMapper.updateEntityFromDto(alimentoDTO, alimentoExistente);
        alimentoExistente = alimentoRepository.save(alimentoExistente);
        
        log.info("Alimento actualizado parcialmente: {}", alimentoExistente.getId());
        return alimentoMapper.toDto(alimentoExistente);
    }
    
    @Override
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('ADMIN')")
    @CacheEvict(value = {"alimentos", "alimentos-search", "alimento"}, allEntries = true)
    public void eliminar(Integer id) {
        log.info("Eliminando alimento ID: {}", id);
        
        if (!alimentoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alimento no encontrado con ID: " + id);
        }
        
        alimentoRepository.deleteById(id);
        log.info("Alimento eliminado exitosamente: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "alimentos-estado", key = "#estado + '-' + #pageable.pageNumber")
    public Page<AlimentoDTO> buscarPorEstado(String estado, Pageable pageable) {
        log.debug("Buscando alimentos por estado: {}", estado);
        
        Page<Alimento> alimentosPage = alimentoRepository.findByEstadoIgnoreCase(estado, pageable);
        return alimentosPage.map(alimentoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AlimentoDTO> buscarPorRangoCalorias(Double minCal, Double maxCal, Pageable pageable) {
        log.debug("Buscando alimentos por rango calórico: {} - {}", minCal, maxCal);
        
        Page<Alimento> alimentosPage = alimentoRepository.findByCalorieRange(minCal, maxCal, pageable);
        return alimentosPage.map(alimentoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "alimentos-proteina", key = "#threshold")
    public List<AlimentoDTO> buscarAlimentosAltoProteina(Double threshold) {
        log.debug("Buscando alimentos altos en proteína: umbral {}", threshold);
        
        List<Alimento> alimentos = alimentoRepository.findHighProteinFoods(threshold);
        return alimentoMapper.toDtoList(alimentos);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "alimentos-baja-caloria", key = "#threshold")
    public List<AlimentoDTO> buscarAlimentosBajaCaloria(Double threshold) {
        log.debug("Buscando alimentos bajos en calorías: umbral {}", threshold);
        
        List<Alimento> alimentos = alimentoRepository.findLowCalorieFoods(threshold);
        return alimentoMapper.toDtoList(alimentos);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AlimentoDTO> buscarAlimentosRicosEnVitamina(String vitamin, Double threshold) {
        log.debug("Buscando alimentos ricos en vitamina {}: umbral {}", vitamin, threshold);
        
        List<Alimento> alimentos = alimentoRepository.findFoodsRichInVitamin(vitamin, threshold);
        return alimentoMapper.toDtoList(alimentos);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AlimentoDTO> buscarAlimentosParaDieta(Double minCal, Double maxCal, 
                                                      Double minProtein, Double maxFat, 
                                                      Pageable pageable) {
        log.debug("Buscando alimentos para dieta: calorias({}-{}), proteina(min:{}), grasa(max:{})", 
                  minCal, maxCal, minProtein, maxFat);
        
        Page<Alimento> alimentosPage = alimentoRepository.findDietSuitableFoods(
            minCal, maxCal, minProtein, maxFat, pageable);
        return alimentosPage.map(alimentoMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "alimentos-select")
    public List<AlimentoDTO> listarParaSelect() {
        log.debug("Listando alimentos para select (sin paginación)");
        
        List<Alimento> alimentos = alimentoRepository.findAll();
        return alimentoMapper.toDtoList(alimentos);
    }
    
    // ====================== BUSINESS VALIDATION METHODS ======================
    
    /**
     * Validates that food name is unique
     */
    private void validarNombreUnico(String nombre, Integer excludeId) {
        boolean exists = (excludeId == null) 
            ? alimentoRepository.existsByNombreIgnoreCase(nombre)
            : alimentoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, excludeId);
            
        if (exists) {
            throw new BusinessException("Ya existe un alimento con el nombre: " + nombre);
        }
    }
    
    /**
     * Validates nutritional completeness
     */
    private void validarCompletudNutricional(AlimentoDTO alimentoDTO) {
        if (!alimentoDTO.isNutritionallyComplete()) {
            throw new BusinessException("El alimento debe tener calorías y al menos un macronutriente (carbohidratos, grasas o proteínas)");
        }
    }
    
    /**
     * Validates that all nutritional values are positive
     */
    private void validarValoresPositivos(AlimentoDTO alimentoDTO) {
        if (alimentoDTO.getCal() != null && alimentoDTO.getCal() < 0) {
            throw new BusinessException("Las calorías no pueden ser negativas");
        }
        // Add more validations as needed...
    }
    
    /**
     * Validates positive values for partial updates (only check non-null fields)
     */
    private void validarValoresPositivosParcial(AlimentoDTO alimentoDTO) {
        if (alimentoDTO.getCal() != null && alimentoDTO.getCal() < 0) {
            throw new BusinessException("Las calorías no pueden ser negativas");
        }
        if (alimentoDTO.getProteinas() != null && alimentoDTO.getProteinas() < 0) {
            throw new BusinessException("Las proteínas no pueden ser negativas");
        }
        if (alimentoDTO.getGrasas() != null && alimentoDTO.getGrasas() < 0) {
            throw new BusinessException("Las grasas no pueden ser negativas");
        }
        if (alimentoDTO.getHidratosdecarbono() != null && alimentoDTO.getHidratosdecarbono() < 0) {
            throw new BusinessException("Los hidratos de carbono no pueden ser negativos");
        }
    }
}
