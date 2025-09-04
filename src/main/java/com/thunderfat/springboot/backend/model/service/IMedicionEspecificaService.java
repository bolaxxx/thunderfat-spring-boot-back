package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;

/**
 * Interfaz que define las operaciones para el servicio de mediciones específicas
 */
public interface IMedicionEspecificaService {
    /**
     * Inserta una medición específica asociada a un paciente
     * 
     * @param medicion La medición específica a insertar
     * @param id_paciente El ID del paciente al que se asocia la medición
     */
    void insertar(MedicionEspecifica medicion, int id_paciente);
    
    /**
     * Elimina una medición específica por su ID
     * 
     * @param id_medicion_espeficica ID de la medición específica a eliminar
     */
    void eliminar(int id_medicion_espeficica);
    
    /**
     * Busca una medición específica por su ID
     * 
     * @param id_medicion_especifica ID de la medición específica a buscar
     * @return La medición específica encontrada, o lanza una excepción si no existe
     */
    MedicionEspecifica buscarPorId(int id_medicion_especifica);
    
    /**
     * Lista todas las mediciones específicas
     * 
     * @return Lista de todas las mediciones específicas
     */
    List<MedicionEspecifica> listar();
    
    /**
     * Lista las mediciones específicas asociadas a un paciente
     * 
     * @param id_paciente ID del paciente
     * @return Lista de mediciones específicas del paciente
     */
    List<MedicionEspecifica> listarPorPaciente(int id_paciente);
}
