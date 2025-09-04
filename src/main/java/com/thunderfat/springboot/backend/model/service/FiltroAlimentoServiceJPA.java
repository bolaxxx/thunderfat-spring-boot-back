package com.thunderfat.springboot.backend.model.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.FiltroAlimentarioRepository;
import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;
import com.thunderfat.springboot.backend.model.dto.FiltroAlimentarioDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.AlimentoMapper;
import com.thunderfat.springboot.backend.model.dto.mapper.FiltroAlimentarioMapper;
import com.thunderfat.springboot.backend.model.entity.Alimento;
import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestionar los filtros alimentarios.
 * Un filtro alimentario es una colección de alimentos agrupados bajo un criterio específico.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FiltroAlimentoServiceJPA implements IFiltroAlimentarioService {
    
    private final FiltroAlimentarioRepository filtroRepository;
    private final AlimentoServiceJPA alimentoService;
    private final AlimentoMapper alimentoMapper;
    private final FiltroAlimentarioMapper filtroMapper;

    // ============= Métodos Legacy (Deprecados) =============

    /**
     * @deprecated Use {@link #findById(int)} instead.
     */
    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public FiltroAlimentario buscarPorId(int id_filtroalimentario) {
        log.debug("Método deprecado: buscarPorId({})", id_filtroalimentario);
        return filtroRepository.findById(id_filtroalimentario).orElse(null);
    }

    /**
     * @deprecated Use {@link #save(FiltroAlimentarioDTO, int)} instead.
     */
    @Override
    @Deprecated
    @Transactional
    public void insertar(FiltroAlimentario filtroalimentario, int id_nutricionista) {
        log.debug("Método deprecado: insertar({}, {})", filtroalimentario, id_nutricionista);
        // Set the nutricionista ID
        filtroalimentario.setId_nutricionista(id_nutricionista);
        filtroRepository.save(filtroalimentario);
    }

    /**
     * @deprecated Use {@link #deleteById(int)} instead.
     */
    @Override
    @Deprecated
    @Transactional
    public void eliminar(int id_filtroalimentario) {
        log.debug("Método deprecado: eliminar({})", id_filtroalimentario);
        filtroRepository.deleteById(id_filtroalimentario);
    }

    /**
     * @deprecated Use {@link #findAll()} instead.
     */
    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<FiltroAlimentario> listarTodos() {
        log.debug("Método deprecado: listarTodos()");
        return filtroRepository.findAll();
    }
    
    // ============= Métodos Modernos =============
    
    /**
     * Encuentra todos los filtros alimentarios.
     *
     * @return Lista de todos los filtros alimentarios como DTOs
     */
    @Transactional(readOnly = true)
    @Cacheable("filtroAlimentarios")
    public List<FiltroAlimentarioDTO> findAll() {
        log.debug("Obteniendo todos los filtros alimentarios");
        return filtroRepository.findAll().stream()
                .map(filtroMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Encuentra un filtro alimentario por su ID.
     *
     * @param id El ID del filtro alimentario
     * @return El DTO del filtro alimentario
     * @throws ResourceNotFoundException si el filtro alimentario no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "filtroAlimentario", key = "#id")
    public FiltroAlimentarioDTO findById(int id) {
        log.debug("Buscando filtro alimentario con ID: {}", id);
        return filtroRepository.findById(id)
                .map(filtroMapper::toDTOWithAlimentos)
                .orElseThrow(() -> new ResourceNotFoundException("Filtro alimentario no encontrado con ID: " + id));
    }
    
    /**
     * Guarda un nuevo filtro alimentario.
     *
     * @param filtroDTO El DTO del filtro alimentario a guardar
     * @param nutricionistaId El ID del nutricionista al que pertenece
     * @return El DTO del filtro alimentario guardado
     */
    @Transactional
    @CacheEvict(value = {"filtroAlimentario", "filtroAlimentarios", "nutricionistaFiltros"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public FiltroAlimentarioDTO save(FiltroAlimentarioDTO filtroDTO, int nutricionistaId) {
        log.debug("Guardando filtro alimentario para nutricionista con ID: {}", nutricionistaId);
        
        FiltroAlimentario filtro = filtroMapper.toEntity(filtroDTO);
        filtro.setId_nutricionista(nutricionistaId);
        
        // Process alimentos if present
        if (filtroDTO.getAlimentos() != null && !filtroDTO.getAlimentos().isEmpty()) {
            List<Alimento> alimentos = filtroDTO.getAlimentos().stream()
                    .map(alimentoDTO -> alimentoMapper.toEntity(alimentoDTO))
                    .collect(Collectors.toList());
            filtro.setAlimentos(alimentos);
        }
        
        FiltroAlimentario savedFiltro = filtroRepository.save(filtro);
        return filtroMapper.toDTOWithAlimentos(savedFiltro);
    }
    
    /**
     * Actualiza un filtro alimentario existente.
     *
     * @param id El ID del filtro alimentario a actualizar
     * @param filtroDTO Los nuevos datos del filtro alimentario
     * @return El DTO del filtro alimentario actualizado
     * @throws ResourceNotFoundException si el filtro alimentario no existe
     */
    @Transactional
    @CacheEvict(value = {"filtroAlimentario", "filtroAlimentarios", "nutricionistaFiltros"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public FiltroAlimentarioDTO update(int id, FiltroAlimentarioDTO filtroDTO) {
        log.debug("Actualizando filtro alimentario con ID: {}", id);
        
        if (!filtroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Filtro alimentario no encontrado con ID: " + id);
        }
        
        FiltroAlimentario filtro = filtroMapper.toEntity(filtroDTO);
        filtro.setId(id);
        
        // Preserve nutricionista ID
        FiltroAlimentario existingFiltro = filtroRepository.findById(id).orElseThrow();
        filtro.setId_nutricionista(existingFiltro.getId_nutricionista());
        
        // Process alimentos if present
        if (filtroDTO.getAlimentos() != null && !filtroDTO.getAlimentos().isEmpty()) {
            List<Alimento> alimentos = filtroDTO.getAlimentos().stream()
                    .map(alimentoDTO -> alimentoMapper.toEntity(alimentoDTO))
                    .collect(Collectors.toList());
            filtro.setAlimentos(alimentos);
        }
        
        FiltroAlimentario updatedFiltro = filtroRepository.save(filtro);
        return filtroMapper.toDTOWithAlimentos(updatedFiltro);
    }
    
    /**
     * Elimina un filtro alimentario por su ID.
     *
     * @param id El ID del filtro alimentario a eliminar
     * @throws ResourceNotFoundException si el filtro alimentario no existe
     */
    @Transactional
    @CacheEvict(value = {"filtroAlimentario", "filtroAlimentarios", "nutricionistaFiltros"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public void deleteById(int id) {
        log.debug("Eliminando filtro alimentario con ID: {}", id);
        
        if (!filtroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Filtro alimentario no encontrado con ID: " + id);
        }
        
        filtroRepository.deleteById(id);
    }

    /**
     * Obtiene información resumida de los alimentos en un filtro específico.
     * 
     * @param id_filtro ID del filtro alimentario
     * @return Lista de mapas con información básica de cada alimento (id y nombre)
     * @throws ResourceNotFoundException si el filtro alimentario no existe
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "filtroAlimentos", key = "#id_filtro")
    public ArrayList<Map<String, Object>> alimentosEnFiltro(int id_filtro) {
        log.debug("Obteniendo alimentos en filtro con ID: {}", id_filtro);
        
        FiltroAlimentario filtro = filtroRepository.findById(id_filtro)
                .orElseThrow(() -> new ResourceNotFoundException("Filtro alimentario no encontrado con ID: " + id_filtro));
        
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        if (filtro.getAlimentos() != null) {
            for (Alimento alimento : filtro.getAlimentos()) {
                Map<String, Object> temp = new LinkedHashMap<>();
                temp.put("id", alimento.getId());
                temp.put("text", alimento.getNombre());
                result.add(temp);
            }
        }
        
        return result;
    }

    /**
     * Inserta un nuevo filtro alimentario usando datos en formato JSON.
     * 
     * @param filtrojson Mapa con los datos del filtro en formato JSON
     * @return ID del filtro alimentario creado
     */
    @Override
    @Transactional
    @CacheEvict(value = {"filtroAlimentario", "filtroAlimentarios", "nutricionistaFiltros", "filtroAlimentos"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public int InsetarAjax(Map<String, Object> filtrojson) {
        log.debug("Insertando filtro alimentario desde JSON: {}", filtrojson);
        
        String nombre = (String) filtrojson.get("nombre");
        String descripcion = filtrojson.get("descripcion") != null ? 
                (String) filtrojson.get("descripcion") : "";
                
        // Parse alimentos IDs from string
        List<Integer> alimentosIds = new ArrayList<>();
        if (filtrojson.get("alimentos") != null) {
            String alimentoString = filtrojson.get("alimentos").toString();
            try {
                String[] items = alimentoString.replaceAll("\\[", "")
                                            .replaceAll("\\]", "")
                                            .replaceAll("\\s", "")
                                            .split(",");
                
                for (String item : items) {
                    if (!item.isEmpty()) {
                        alimentosIds.add(Integer.parseInt(item));
                    }
                }
            } catch (NumberFormatException e) {
                log.error("Error parsing alimentos IDs: {}", e.getMessage());
                throw new IllegalArgumentException("Formato inválido de IDs de alimentos: " + e.getMessage());
            }
        }
        
        // Build alimentos list
        List<Alimento> alimentos = new ArrayList<>();
        for (Integer alimentoId : alimentosIds) {
            AlimentoDTO alimentoDTO = alimentoService.buscarPorId(alimentoId);
            if (alimentoDTO != null) {
                alimentos.add(alimentoMapper.toEntity(alimentoDTO));
            }
        }
        
        // Create and save the filtro
        FiltroAlimentario filtroAlimentario = new FiltroAlimentario();
        filtroAlimentario.setAlimentos(alimentos);
        filtroAlimentario.setNombre(nombre);
        filtroAlimentario.setDescripcion(descripcion);
        
        // Set nutricionista ID if provided
        if (filtrojson.containsKey("id_nutricionista")) {
            filtroAlimentario.setId_nutricionista((int) filtrojson.get("id_nutricionista"));
        }
        
        // Set ID if updating an existing filtro
        if (filtrojson.containsKey("id_filtro")) {
            filtroAlimentario.setId((int) filtrojson.get("id_filtro"));
        }
        
        // Save and return the ID
        FiltroAlimentario savedFiltro = filtroRepository.save(filtroAlimentario);
        return savedFiltro.getId();
    }

    /**
     * Encuentra todos los filtros alimentarios para un nutricionista específico.
     *
     * @param id_nutricionista El ID del nutricionista
     * @return Lista de filtros alimentarios
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "nutricionistaFiltros", key = "#id_nutricionista")
    public List<FiltroAlimentario> listarporNutricionista(int id_nutricionista) {
        log.debug("Buscando filtros alimentarios para nutricionista con ID: {}", id_nutricionista);
        return filtroRepository.buscarpornutricionista(id_nutricionista);
    }
    
    /**
     * Encuentra todos los filtros alimentarios para un nutricionista específico como DTOs.
     *
     * @param nutricionistaId El ID del nutricionista
     * @return Lista de filtros alimentarios como DTOs
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "nutricionistaFiltrosDTO", key = "#nutricionistaId")
    @PreAuthorize("hasRole('NUTRICIONISTA') and #nutricionistaId == authentication.principal.id")
    public List<FiltroAlimentarioDTO> findByNutricionistaId(int nutricionistaId) {
        log.debug("Buscando filtros alimentarios como DTOs para nutricionista con ID: {}", nutricionistaId);
        
        return filtroRepository.buscarpornutricionista(nutricionistaId).stream()
                .map(filtroMapper::toDTOWithAlimentos)
                .collect(Collectors.toList());
    }
}
