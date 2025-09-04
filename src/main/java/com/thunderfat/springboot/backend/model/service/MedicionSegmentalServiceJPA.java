package com.thunderfat.springboot.backend.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.MedicionSegmentalRepository;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.PacienteMapper;
import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA implementation of the MedicionSegmental service interface.
 * Follows Spring Boot 2025 best practices including:
 * - Modern repository integration
 * - Transaction management
 * - Proper error handling
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedicionSegmentalServiceJPA implements IMedicionSegmentalService {

    private final MedicionSegmentalRepository repo;
    private final PacienteServiceJPA servicePaciente;

    @Override
    @Transactional
    @CacheEvict(value = "mediciones-segmentales", allEntries = true)
    public void eliminar(int id_medicion_segmental) {
        if (!repo.existsById(id_medicion_segmental)) {
            throw new ResourceNotFoundException("MedicionSegmental not found with id: " + id_medicion_segmental);
        }
        repo.deleteById(id_medicion_segmental);
    }

    @Override
    @Transactional
    @CacheEvict(value = "mediciones-segmentales", key = "#id_paciente")
    public void insertar(MedicionSegmental medicion_general, int id_paciente) {
        if (medicion_general == null) {
            throw new IllegalArgumentException("MedicionSegmental cannot be null");
        }
        PacienteDTO pacienteDTO = servicePaciente.buscarPorId(id_paciente);
        if (pacienteDTO == null) {
            throw new ResourceNotFoundException("Paciente not found with id: " + id_paciente);
        }
        Paciente paciente = PacienteMapper.INSTANCE.toEntity(pacienteDTO);
        medicion_general.setId_paciente(id_paciente);
        // persist measurement directly for simplicity
        repo.save(medicion_general);
        // Also maintain relationship on Paciente side if needed
        try {
            paciente.getMedicionessegmentales().add(medicion_general);
            PacienteDTO pacienteDTOUpdated = PacienteMapper.INSTANCE.toDto(paciente);
            servicePaciente.insertar(pacienteDTOUpdated);
        } catch (Exception ex) {
            log.debug("Non-critical: failed to update paciente relationship: {}", ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicionSegmental> listar() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicionSegmental> listarPorPaciente(int id_paciente) {
        return repo.findByPacienteId(id_paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicionSegmental buscarPorID(int id_medicion_general) {
        Optional<MedicionSegmental> optional = repo.findById(id_medicion_general);
        return optional.orElse(null);
    }

    @Override
    @Transactional
    public void actualizar(MedicionSegmental medicion, int id_paciente) {
        if (medicion == null) {
            throw new IllegalArgumentException("MedicionSegmental cannot be null");
        }
        log.info("Updating MedicionSegmental id={} for paciente={}", medicion.getId(), id_paciente);

        Optional<MedicionSegmental> existing = repo.findById(medicion.getId());
        if (existing.isEmpty()) {
            throw new ResourceNotFoundException("MedicionSegmental not found with id: " + medicion.getId());
        }

        MedicionSegmental current = existing.get();
        if (current.getId_paciente() != id_paciente) {
            throw new IllegalArgumentException("MedicionSegmental does not belong to paciente: " + id_paciente);
        }

        // Merge allowed fields
        current.setFecha(medicion.getFecha());
        current.setBdporcentajegrasas(medicion.getBdporcentajegrasas());
        current.setBdmusculo(medicion.getBdmusculo());
        current.setBimusculo(medicion.getBimusculo());
        current.setPiporcentajegrasas(medicion.getPiporcentajegrasas());
        current.setPdmusculo(medicion.getPdmusculo());
        current.setPdporcentajegrasas(medicion.getPdporcentajegrasas());
        current.setTporcentajegrasa(medicion.getTporcentajegrasa());
        current.setTmusculo(medicion.getTmusculo());
        current.setPimusculo(medicion.getPimusculo());
        current.setBiporcentajegrasas(medicion.getBiporcentajegrasas());

        repo.save(current);
        log.info("MedicionSegmental id={} updated successfully", medicion.getId());
    }

    // ======================
    // Modern API implementations
    // ======================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "mediciones-segmentales", key = "#pacienteId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<MedicionSegmental> findByPacienteId(Integer pacienteId, Pageable pageable) {
        return repo.findByPacienteId(pacienteId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "mediciones-segmentales", key = "'latest_' + #pacienteId")
    public Optional<MedicionSegmental> findLatestByPacienteId(Integer pacienteId) {
        return repo.findLatestByPacienteId(pacienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicionSegmental> findByPacienteIdAndDateRange(Integer pacienteId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return repo.findByPacienteIdAndDateRange(pacienteId, startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageBodyFatPercentage(Integer pacienteId, LocalDate startDate, LocalDate endDate) {
        return repo.getAverageBodyFatPercentage(pacienteId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageMuscleMass(Integer pacienteId, LocalDate startDate, LocalDate endDate) {
        return repo.getAverageMuscleMass(pacienteId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByPacienteId(Integer pacienteId) {
        return repo.countByPacienteId(pacienteId);
    }
}
