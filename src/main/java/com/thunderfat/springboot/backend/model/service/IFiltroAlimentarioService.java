package com.thunderfat.springboot.backend.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thunderfat.springboot.backend.model.dto.FiltroAlimentarioDTO;
import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;

/**
 * Interfaz para el servicio de gestión de filtros alimentarios.
 */
public interface IFiltroAlimentarioService {
	
    /**
     * @deprecated Use {@link #findByNutricionistaId(int)} instead.
     */
    @Deprecated
	List<FiltroAlimentario> listarporNutricionista(int id_nutricionista);
	
    /**
     * @deprecated Use {@link #findById(int)} instead.
     */
    @Deprecated
	FiltroAlimentario buscarPorId(int id_filtroalimentario);
	
    /**
     * @deprecated Use {@link #save(FiltroAlimentarioDTO, int)} instead.
     */
    @Deprecated
	void insertar(FiltroAlimentario filtroalimentario, int id_nutricionista);
	
    /**
     * @deprecated Use {@link #deleteById(int)} instead.
     */
    @Deprecated
	void eliminar(int id_filtroalimentario);
	
    /**
     * @deprecated Use {@link #findAll()} instead.
     */
    @Deprecated
	List<FiltroAlimentario> listarTodos();
	
	/**
	 * Obtiene una lista de mapas con información básica de los alimentos en un filtro.
	 * 
	 * @param id_filtro ID del filtro alimentario
	 * @return Lista de mapas con información resumida de cada alimento
	 */
	ArrayList<Map<String, Object>> alimentosEnFiltro(int id_filtro);
	
	/**
	 * Inserta un filtro alimentario a partir de datos en formato JSON.
	 * 
	 * @param filtrojson Mapa con los datos del filtro
	 * @return ID del filtro insertado
	 */
	int InsetarAjax(Map<String, Object> filtrojson);
    
    /**
     * Encuentra todos los filtros alimentarios.
     *
     * @return Lista de todos los filtros alimentarios como DTOs
     */
    List<FiltroAlimentarioDTO> findAll();
    
    /**
     * Encuentra un filtro alimentario por su ID.
     *
     * @param id El ID del filtro alimentario
     * @return El DTO del filtro alimentario
     */
    FiltroAlimentarioDTO findById(int id);
    
    /**
     * Guarda un nuevo filtro alimentario.
     *
     * @param filtroDTO El DTO del filtro alimentario a guardar
     * @param nutricionistaId El ID del nutricionista al que pertenece
     * @return El DTO del filtro alimentario guardado
     */
    FiltroAlimentarioDTO save(FiltroAlimentarioDTO filtroDTO, int nutricionistaId);
    
    /**
     * Actualiza un filtro alimentario existente.
     *
     * @param id El ID del filtro alimentario a actualizar
     * @param filtroDTO Los nuevos datos del filtro alimentario
     * @return El DTO del filtro alimentario actualizado
     */
    FiltroAlimentarioDTO update(int id, FiltroAlimentarioDTO filtroDTO);
    
    /**
     * Elimina un filtro alimentario por su ID.
     *
     * @param id El ID del filtro alimentario a eliminar
     */
    void deleteById(int id);
    
    /**
     * Encuentra todos los filtros alimentarios para un nutricionista específico.
     *
     * @param nutricionistaId El ID del nutricionista
     * @return Lista de filtros alimentarios como DTOs
     */
    List<FiltroAlimentarioDTO> findByNutricionistaId(int nutricionistaId);
}
