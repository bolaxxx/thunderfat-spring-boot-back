package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;

/**
 * Service interface for managing general body measurements.
 * Handles comprehensive patient measurement operations including weight tracking,
 * BMI calculation, and progress monitoring for nutritional assessment.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 2025
 */
public interface IMedicionGeneralService {
    
    /**
     * Creates a new general measurement for a patient.
     * 
     * @param medicionGeneral the measurement to create
     * @param idPaciente the ID of the patient
     * @return the saved measurement
     */
    MedicionGeneral insertar(MedicionGeneral medicionGeneral, int idPaciente);
    
    /**
     * Deletes a general measurement by ID.
     * 
     * @param idMedicionGeneral the ID of the measurement to delete
     */
    void eliminar(int idMedicionGeneral);
    
    /**
     * Retrieves all general measurements in the system.
     * 
     * @return list of all measurements
     */
    List<MedicionGeneral> listar();
    
    /**
     * Retrieves all measurements for a specific patient.
     * 
     * @param idPaciente the ID of the patient
     * @return list of measurements for the patient
     */
    List<MedicionGeneral> listarPorPaciente(int idPaciente);
    
    /**
     * Retrieves measurements for a patient with pagination.
     * 
     * @param idPaciente the ID of the patient
     * @param pageable pagination information
     * @return paginated measurements
     */
    Page<MedicionGeneral> listarPorPacientePaginado(int idPaciente, Pageable pageable);
    
    /**
     * Finds a measurement by its ID.
     * 
     * @param idMedicionGeneral the ID of the measurement
     * @return the measurement if found
     */
    Optional<MedicionGeneral> buscarPorId(int idMedicionGeneral);
    
    /**
     * Gets the most recent measurement for a patient.
     * 
     * @param idPaciente the ID of the patient
     * @return the latest measurement if found
     */
    Optional<MedicionGeneral> buscarUltimaMedicion(int idPaciente);
    
    /**
     * Updates an existing general measurement.
     * 
     * @param medicion the measurement to update
     */
    void actualizar(MedicionGeneral medicion);
    
    /**
     * Gets the most recent weight value for a patient.
     * 
     * @param idPaciente the ID of the patient
     * @return the latest weight value, or null if no measurements exist
     */
    Double buscarUltimoPeso(int idPaciente);
    
    /**
     * Gets weight history for trend analysis.
     * 
     * @param idPaciente the ID of the patient
     * @return list of weight measurements with dates
     */
    List<Object> obtenerHistorialPeso(int idPaciente);
    
    /**
     * Gets BMI history for trend analysis.
     * 
     * @param idPaciente the ID of the patient
     * @return list of BMI measurements with dates
     */
    List<Object> obtenerHistorialBmi(int idPaciente);
    
    /**
     * Finds measurements within a date range for a patient.
     * 
     * @param idPaciente the ID of the patient
     * @param fechaInicio start date
     * @param fechaFin end date
     * @return list of measurements in the date range
     */
    List<MedicionGeneral> buscarPorRangoFecha(int idPaciente, LocalDate fechaInicio, LocalDate fechaFin);
    
    /**
     * Gets the baseline measurement (first recorded) for a patient.
     * 
     * @param idPaciente the ID of the patient
     * @return the first measurement if found
     */
    Optional<MedicionGeneral> buscarMedicionBaseline(int idPaciente);
}
