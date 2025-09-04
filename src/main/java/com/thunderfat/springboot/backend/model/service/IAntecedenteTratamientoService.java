package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.dto.AntecedenteTratamientoDTO;
import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;

public interface IAntecedenteTratamientoService {
    /**
     * @deprecated Use {@link #findAll()} instead.
     */
    @Deprecated
    List<AntecedenteTratamiento> listarAntecedentes_Tratamiento();
    
    /**
     * @deprecated Use {@link #findById(int)} instead.
     */
    @Deprecated
    AntecedenteTratamiento buscarPorId(int id_antecedente_tratamiento);
    
    /**
     * @deprecated Use {@link #findByPatientId(int)} instead.
     */
    @Deprecated
    List<AntecedenteTratamiento> buscarPorPaciente(int id_paciente);
    
    /**
     * @deprecated Use {@link #save(AntecedenteTratamientoDTO, int)} instead.
     */
    @Deprecated
    void insertar(AntecedenteTratamiento antecedente_tratamiento, int id_paciente);
    
    /**
     * @deprecated Use {@link #deleteById(int)} instead.
     */
    @Deprecated
    void eliminar(int id_antecedente_tratamiento);
    
    /**
     * Encuentra todos los antecedentes de tratamiento.
     *
     * @return Lista de todos los antecedentes de tratamiento como DTOs
     */
    List<AntecedenteTratamientoDTO> findAll();
    
    /**
     * Encuentra un antecedente de tratamiento por su ID.
     *
     * @param id El ID del antecedente de tratamiento
     * @return El DTO del antecedente de tratamiento
     * @throws ResourceNotFoundException si el antecedente de tratamiento no existe
     */
    AntecedenteTratamientoDTO findById(int id);
    
    /**
     * Guarda un nuevo antecedente de tratamiento para un paciente.
     *
     * @param antecedenteTratamientoDTO El DTO del antecedente de tratamiento a guardar
     * @param idPaciente El ID del paciente asociado
     * @return El DTO del antecedente de tratamiento guardado
     * @throws ResourceNotFoundException si el paciente no existe
     */
    AntecedenteTratamientoDTO save(AntecedenteTratamientoDTO antecedenteTratamientoDTO, int idPaciente);
    
    /**
     * Actualiza un antecedente de tratamiento existente.
     *
     * @param id El ID del antecedente de tratamiento a actualizar
     * @param antecedenteTratamientoDTO Los nuevos datos del antecedente de tratamiento
     * @return El DTO del antecedente de tratamiento actualizado
     * @throws ResourceNotFoundException si el antecedente de tratamiento no existe
     */
    AntecedenteTratamientoDTO update(int id, AntecedenteTratamientoDTO antecedenteTratamientoDTO);
    
    /**
     * Elimina un antecedente de tratamiento por su ID.
     *
     * @param id El ID del antecedente de tratamiento a eliminar
     * @throws ResourceNotFoundException si el antecedente de tratamiento no existe
     */
    void deleteById(int id);
    
    /**
     * Encuentra todos los antecedentes de tratamiento de un paciente.
     *
     * @param idPaciente El ID del paciente
     * @return Lista de antecedentes de tratamiento como DTOs
     * @throws ResourceNotFoundException si el paciente no existe
     */
    List<AntecedenteTratamientoDTO> findByPatientId(int idPaciente);
}
