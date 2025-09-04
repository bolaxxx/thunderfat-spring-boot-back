package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.dto.DiaDietaDTO;
import com.thunderfat.springboot.backend.model.entity.DiaDieta;

/**
 * Interfaz para el servicio de gestión de días de dieta.
 */
public interface IDIaDietaService {
    
    /**
     * @deprecated Use {@link #save(DiaDietaDTO)} instead.
     */
    @Deprecated
    DiaDieta insertar(DiaDieta dia);
    
    /**
     * @deprecated Use {@link #findById(int)} instead.
     */
    @Deprecated
    DiaDieta buscarporID(int id);
    
    /**
     * @deprecated Use {@link #findByPlanId(int)} instead.
     */
    @Deprecated
    List<DiaDieta> buscarporPlan(int id_plan);
    
    /**
     * @deprecated Use {@link #deleteById(int)} instead.
     */
    @Deprecated
    void eliminar(int id_dia);
    
    /**
     * Encuentra todos los días de dieta.
     *
     * @return Lista de todos los días de dieta como DTOs
     */
    List<DiaDietaDTO> findAll();
    
    /**
     * Encuentra un día de dieta por su ID.
     *
     * @param id El ID del día de dieta
     * @return El DTO del día de dieta
     * @throws ResourceNotFoundException si el día de dieta no existe
     */
    DiaDietaDTO findById(int id);
    
    /**
     * Guarda un nuevo día de dieta.
     *
     * @param diaDietaDTO El DTO del día de dieta a guardar
     * @return El DTO del día de dieta guardado
     */
    DiaDietaDTO save(DiaDietaDTO diaDietaDTO);
    
    /**
     * Guarda un nuevo día de dieta en un plan específico.
     *
     * @param diaDietaDTO El DTO del día de dieta a guardar
     * @param planId El ID del plan de dieta al que pertenece
     * @return El DTO del día de dieta guardado
     * @throws ResourceNotFoundException si el plan de dieta no existe
     */
    DiaDietaDTO saveForPlan(DiaDietaDTO diaDietaDTO, int planId);
    
    /**
     * Actualiza un día de dieta existente.
     *
     * @param id El ID del día de dieta a actualizar
     * @param diaDietaDTO Los nuevos datos del día de dieta
     * @return El DTO del día de dieta actualizado
     * @throws ResourceNotFoundException si el día de dieta no existe
     */
    DiaDietaDTO update(int id, DiaDietaDTO diaDietaDTO);
    
    /**
     * Elimina un día de dieta por su ID.
     *
     * @param id El ID del día de dieta a eliminar
     * @throws ResourceNotFoundException si el día de dieta no existe
     */
    void deleteById(int id);
    
    /**
     * Encuentra los días de dieta para un plan específico.
     *
     * @param planId El ID del plan de dieta
     * @return Lista de días de dieta como DTOs
     * @throws ResourceNotFoundException si el plan de dieta no existe
     */
    List<DiaDietaDTO> findByPlanId(int planId);
}
