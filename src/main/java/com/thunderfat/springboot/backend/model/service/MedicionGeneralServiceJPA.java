package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.MedicionGeneralRepository;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.PacienteMapper;
import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.entity.Paciente;

/**
 * JPA implementation of the general measurements service.
 * Manages comprehensive patient measurement operations including weight tracking,
 * BMI calculation, and progress monitoring for nutritional assessment.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 2025
 */
@Service
public class MedicionGeneralServiceJPA implements IMedicionGeneralService {
    
    @Autowired
    private MedicionGeneralRepository repo;
    
    @Autowired
    private PacienteServiceJPA pacienteService;

    @Override
    @Transactional
    public MedicionGeneral insertar(MedicionGeneral medicionGeneral, int idPaciente) {
        Optional<PacienteDTO> pacienteDTOOpt = this.pacienteService.findById(Integer.valueOf(idPaciente));
        if (pacienteDTOOpt.isEmpty()) {
            throw new RuntimeException("Patient not found with ID: " + idPaciente);
        }
        
        PacienteDTO pacienteDTO = pacienteDTOOpt.get();
        Paciente paciente = PacienteMapper.INSTANCE.toEntity(pacienteDTO);
        paciente.getMedicionesgenerales().add(medicionGeneral);
        PacienteDTO pacienteDTOUpdated = PacienteMapper.INSTANCE.toDto(paciente);
        pacienteService.update(Integer.valueOf(idPaciente), pacienteDTOUpdated);
        
        return paciente.getMedicionesgenerales().get(paciente.getMedicionesgenerales().size() - 1);
    }

    @Override
    @Transactional
    public void eliminar(int idMedicionGeneral) {
        repo.deleteById(Integer.valueOf(idMedicionGeneral));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicionGeneral> listar() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicionGeneral> listarPorPaciente(int idPaciente) {
        return repo.findByPacienteId(Integer.valueOf(idPaciente));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicionGeneral> listarPorPacientePaginado(int idPaciente, Pageable pageable) {
        return repo.findByPacienteId(Integer.valueOf(idPaciente), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicionGeneral> buscarPorId(int idMedicionGeneral) {
        return repo.findById(Integer.valueOf(idMedicionGeneral));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicionGeneral> buscarUltimaMedicion(int idPaciente) {
        return repo.findLatestByPacienteId(Integer.valueOf(idPaciente));
    }

    @Override
    @Transactional
    public void actualizar(MedicionGeneral medicion) {
        repo.save(medicion);
    }

    @Override
    @Transactional(readOnly = true)
    public Double buscarUltimoPeso(int idPaciente) {
        return repo.findLatestWeightByPacienteId(Integer.valueOf(idPaciente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> obtenerHistorialPeso(int idPaciente) {
        return repo.findWeightHistoryByPacienteId(Integer.valueOf(idPaciente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> obtenerHistorialBmi(int idPaciente) {
        return repo.findBmiHistoryByPacienteId(Integer.valueOf(idPaciente));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicionGeneral> buscarPorRangoFecha(int idPaciente, LocalDate fechaInicio, LocalDate fechaFin) {
        return repo.findByPacienteIdAndFechaBetween(Integer.valueOf(idPaciente), fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicionGeneral> buscarMedicionBaseline(int idPaciente) {
        return repo.findFirstByIdPacienteOrderByFechaAsc(Integer.valueOf(idPaciente));
    }
}
