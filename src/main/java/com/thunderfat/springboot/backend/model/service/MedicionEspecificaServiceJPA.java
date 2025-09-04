package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.MedicionEspecificaRepository;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.PacienteMapper;
import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;
import com.thunderfat.springboot.backend.model.entity.Paciente;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la gestión de mediciones específicas de pacientes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MedicionEspecificaServiceJPA implements IMedicionEspecificaService {
    private final MedicionEspecificaRepository repo;
    private final PacienteServiceJPA servicepaciente;

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"mediciones", "medicionesPaciente"}, allEntries = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_NUTRICIONISTA')")
    public void eliminar(int id_medicion_espeficica) {
        log.debug("Eliminando medición específica con ID: {}", id_medicion_espeficica);
        if (!repo.existsById(id_medicion_espeficica)) {
            throw new ResourceNotFoundException("No se encontró la medición específica con ID: " + id_medicion_espeficica);
        }
        repo.deleteById(id_medicion_espeficica);
        log.info("Medición específica con ID: {} eliminada exitosamente", id_medicion_espeficica);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "mediciones", key = "#id_medicion_especifica")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_NUTRICIONISTA') or hasRole('ROLE_PACIENTE')")
    public MedicionEspecifica buscarPorId(int id_medicion_especifica) {
        log.debug("Buscando medición específica por ID: {}", id_medicion_especifica);
        return repo.findById(id_medicion_especifica)
            .orElseThrow(() -> new ResourceNotFoundException("No se encontró la medición específica con ID: " + id_medicion_especifica));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "mediciones")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<MedicionEspecifica> listar() {
        log.debug("Listando todas las mediciones específicas");
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "medicionesPaciente", key = "#id_paciente")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_NUTRICIONISTA') or hasRole('ROLE_PACIENTE')")
    public List<MedicionEspecifica> listarPorPaciente(int id_paciente) {
        log.debug("Listando mediciones específicas para paciente con ID: {}", id_paciente);
        List<MedicionEspecifica> mediciones = repo.findByPacienteId(id_paciente);
        log.info("Se encontraron {} mediciones para el paciente con ID: {}", mediciones.size(), id_paciente);
        return mediciones;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"mediciones", "medicionesPaciente"}, allEntries = true)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_NUTRICIONISTA')")
    public void insertar(MedicionEspecifica medicion, int id_paciente) {
        log.debug("Insertando medición específica para paciente con ID: {}", id_paciente);
        
        PacienteDTO pacDTO = servicepaciente.buscarPorId(id_paciente);
        if (pacDTO == null) {
            throw new ResourceNotFoundException("No se encontró el paciente con ID: " + id_paciente);
        }
        
        Paciente pac = PacienteMapper.INSTANCE.toEntity(pacDTO);
        pac.getMedicionesespecificas().add(medicion);
        PacienteDTO pacDTOUpdated = PacienteMapper.INSTANCE.toDto(pac);
        servicepaciente.insertar(pacDTOUpdated);
        
        log.info("Medición específica insertada exitosamente para paciente con ID: {}", id_paciente);
    }


}
