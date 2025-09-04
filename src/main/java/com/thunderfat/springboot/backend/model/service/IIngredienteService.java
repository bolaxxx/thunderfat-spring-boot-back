package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.dto.IngredienteDTO;

/**
 * Servicio para la gestión de ingredientes
 */
public interface IIngredienteService {
    /**
     * Lista todos los ingredientes
     * @return Lista de DTOs de ingredientes
     */
    List<IngredienteDTO> listar();
    
    /**
     * Busca un ingrediente por su ID
     * @param id ID del ingrediente
     * @return DTO del ingrediente encontrado
     */
    IngredienteDTO buscarPorId(int id);
    
    /**
     * Inserta o actualiza un ingrediente
     * @param ingredienteDTO DTO del ingrediente a insertar o actualizar
     */
    void insertar(IngredienteDTO ingredienteDTO);
    
    /**
     * Elimina un ingrediente por su ID
     * @param id ID del ingrediente a eliminar
     */
    void eliminar(int id);
    
    /**
     * Calcula los valores nutricionales para un ingrediente
     * @param ingredienteDTO DTO del ingrediente con alimento y cantidad
     * @return DTO del ingrediente con valores nutricionales calculados
     */
    IngredienteDTO calcularValoresNutricionales(IngredienteDTO ingredienteDTO);
}
