package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.IngredienteRepository;
import com.thunderfat.springboot.backend.model.dto.IngredienteDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.IngredienteMapper;
import com.thunderfat.springboot.backend.model.entity.Ingrediente;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestionar los ingredientes.
 * Un ingrediente representa un alimento con una cantidad específica y sus valores nutricionales calculados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IngredienteServiceJPA implements IIngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final IngredienteMapper ingredienteMapper;

    /**
     * Obtiene todos los ingredientes.
     * 
     * @return Lista de DTOs de ingredientes
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable("ingredientes")
    public List<IngredienteDTO> listar() {
        log.debug("Obteniendo todos los ingredientes");
        List<Ingrediente> ingredientes = ingredienteRepository.findAll();
        return ingredientes.stream()
                .map(ingredienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un ingrediente por su ID.
     * 
     * @param id ID del ingrediente
     * @return DTO del ingrediente o null si no existe
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "ingrediente", key = "#id")
    public IngredienteDTO buscarPorId(int id) {
        log.debug("Buscando ingrediente con ID: {}", id);
        return ingredienteRepository.findById(id)
                .map(ingredienteMapper::toDTO)
                .orElse(null);
    }

    /**
     * Inserta un nuevo ingrediente.
     * 
     * @param ingredienteDTO DTO del ingrediente a insertar
     */
    @Override
    @Transactional
    @CacheEvict(value = {"ingrediente", "ingredientes"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public void insertar(IngredienteDTO ingredienteDTO) {
        log.debug("Insertando nuevo ingrediente: {}", ingredienteDTO);
        Ingrediente ingrediente = ingredienteMapper.toEntity(ingredienteDTO);
        ingredienteRepository.save(ingrediente);
    }

    /**
     * Elimina un ingrediente por su ID.
     * 
     * @param id ID del ingrediente a eliminar
     */
    @Override
    @Transactional
    @CacheEvict(value = {"ingrediente", "ingredientes"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public void eliminar(int id) {
        log.debug("Eliminando ingrediente con ID: {}", id);
        if (!ingredienteRepository.existsById(id)) {
            log.warn("Intento de eliminar ingrediente inexistente con ID: {}", id);
            throw new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id);
        }
        ingredienteRepository.deleteById(id);
    }
    
    /**
     * Actualiza un ingrediente existente.
     * 
     * @param id ID del ingrediente a actualizar
     * @param ingredienteDTO Nuevos datos del ingrediente
     * @return DTO del ingrediente actualizado
     * @throws ResourceNotFoundException si el ingrediente no existe
     */
    @Transactional
    @CacheEvict(value = {"ingrediente", "ingredientes"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public IngredienteDTO actualizar(int id, IngredienteDTO ingredienteDTO) {
        log.debug("Actualizando ingrediente con ID: {}", id);
        
        if (!ingredienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id);
        }
        
        Ingrediente ingrediente = ingredienteMapper.toEntity(ingredienteDTO);
        ingrediente.setId(id); // Asegurar que se actualiza el ID correcto
        
        Ingrediente ingredienteActualizado = ingredienteRepository.save(ingrediente);
        return ingredienteMapper.toDTO(ingredienteActualizado);
    }
    
    /**
     * Encuentra un ingrediente por su ID y lanza una excepción si no existe.
     * 
     * @param id ID del ingrediente
     * @return DTO del ingrediente
     * @throws ResourceNotFoundException si el ingrediente no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ingrediente", key = "#id")
    @PreAuthorize("hasAnyRole('NUTRICIONISTA', 'USER')")
    public IngredienteDTO findById(int id) {
        log.debug("Buscando ingrediente con ID: {}", id);
        return ingredienteRepository.findById(id)
                .map(ingredienteMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id));
    }
    
    /**
     * Calcula los valores nutricionales para un ingrediente basado en el alimento y la cantidad.
     * Este método es útil para calcular los valores nutricionales antes de guardar un ingrediente.
     * 
     * @param ingredienteDTO DTO del ingrediente con alimento y cantidad
     * @return DTO del ingrediente con valores nutricionales calculados
     */
    public IngredienteDTO calcularValoresNutricionales(IngredienteDTO ingredienteDTO) {
        log.debug("Calculando valores nutricionales para ingrediente: {}", ingredienteDTO);
        
        if (ingredienteDTO.getAlimento() == null) {
            throw new IllegalArgumentException("El ingrediente debe tener un alimento asociado");
        }
        
        double cantidad = ingredienteDTO.getCantidad();
        double factor = cantidad / 100.0; // Los valores nutricionales son por 100g
        
        // Calcular valores nutricionales basados en el alimento y la cantidad
        ingredienteDTO.setProteinastotales(ingredienteDTO.getAlimento().getProteinas() * factor);
        ingredienteDTO.setGrasastotales(ingredienteDTO.getAlimento().getGrasas() * factor);
        ingredienteDTO.setKcaltotales(ingredienteDTO.getAlimento().getCal() * factor);
        ingredienteDTO.setHidratostotales(ingredienteDTO.getAlimento().getHidratosdecarbono() * factor);
        
        return ingredienteDTO;
    }
}
