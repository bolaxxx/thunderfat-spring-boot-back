package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.AntecedenteTratamientoRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dto.AntecedenteTratamientoDTO;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.AntecedenteTratamientoMapper;
import com.thunderfat.springboot.backend.model.dto.mapper.PacienteMapper;
import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;
import com.thunderfat.springboot.backend.model.entity.Paciente;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AntecedenteTratamientoServiceJPA implements IAntecedenteTratamientoService {
    
    private final AntecedenteTratamientoRepository antecedentesRepo;
    private final PacienteServiceJPA pacienteService;
    private final PacienteRepository pacienteRepository;
    private final AntecedenteTratamientoMapper antecedenteTratamientoMapper;
    private final SecurityService securityService;

    // ============= Métodos Legacy (Deprecados) =============
    
    /**
     * @deprecated Use {@link #findAll()} instead.
     */
    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<AntecedenteTratamiento> listarAntecedentes_Tratamiento() {
        log.debug("Método deprecado: listarAntecedentes_Tratamiento()");
        return antecedentesRepo.findAll();
    }

    /**
     * @deprecated Use {@link #findById(int)} instead.
     */
    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public AntecedenteTratamiento buscarPorId(int id_antecedente_tratamiento) {
        log.debug("Método deprecado: buscarPorId({})", id_antecedente_tratamiento);
        return antecedentesRepo.findById(id_antecedente_tratamiento)
                .orElse(null);
    }

    /**
     * @deprecated Use {@link #save(AntecedenteTratamientoDTO, int)} instead.
     */
    @Override
    @Deprecated
    @Transactional
    public void insertar(AntecedenteTratamiento antecedente_tratamiento, int id_paciente) {
        log.debug("Método deprecado: insertar(AntecedenteTratamiento, {})", id_paciente);
        PacienteDTO pacDTO = pacienteService.buscarPorId(id_paciente);
        Paciente pac = PacienteMapper.INSTANCE.toEntity(pacDTO);
        pac.getAntecedentestratamientos().add(antecedente_tratamiento);
        PacienteDTO pacDTOUpdated = PacienteMapper.INSTANCE.toDto(pac);
        pacienteService.insertar(pacDTOUpdated);
    }

    /**
     * @deprecated Use {@link #deleteById(int)} instead.
     */
    @Override
    @Deprecated
    @Transactional
    public void eliminar(int id_antecedente_tratamiento) {
        log.debug("Método deprecado: eliminar({})", id_antecedente_tratamiento);
        antecedentesRepo.deleteById(id_antecedente_tratamiento);
    }

    /**
     * @deprecated Use {@link #findByPatientId(int)} instead.
     */
    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<AntecedenteTratamiento> buscarPorPaciente(int id_paciente) {
        log.debug("Método deprecado: buscarPorPaciente({})", id_paciente);
        return antecedentesRepo.findByPacienteId(id_paciente);
    }
    
    // ============= Métodos Modernos =============
    
    /**
     * Encuentra todos los antecedentes de tratamiento.
     *
     * @return Lista de todos los antecedentes de tratamiento como DTOs
     */
    @Transactional(readOnly = true)
    @Cacheable("antecedenteTratamientos")
    public List<AntecedenteTratamientoDTO> findAll() {
        log.debug("Obteniendo todos los antecedentes de tratamiento");
        return antecedentesRepo.findAll().stream()
                .map(antecedenteTratamientoMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Encuentra un antecedente de tratamiento por su ID.
     *
     * @param id El ID del antecedente de tratamiento
     * @return El DTO del antecedente de tratamiento
     * @throws ResourceNotFoundException si el antecedente de tratamiento no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "antecedenteTratamiento", key = "#id")
    public AntecedenteTratamientoDTO findById(int id) {
        log.debug("Buscando antecedente de tratamiento con ID: {}", id);
        return antecedentesRepo.findById(id)
                .map(antecedenteTratamientoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Antecedente de tratamiento no encontrado con ID: " + id));
    }
    
    /**
     * Guarda un nuevo antecedente de tratamiento para un paciente.
     *
     * @param antecedenteTratamientoDTO El DTO del antecedente de tratamiento a guardar
     * @param idPaciente El ID del paciente asociado
     * @return El DTO del antecedente de tratamiento guardado
     * @throws ResourceNotFoundException si el paciente no existe
     */
    @Transactional
    @CacheEvict(value = {"antecedenteTratamientos", "pacienteAntecedenteTratamientos"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteAssignedToNutricionista(#idPaciente, authentication.name)")
    public AntecedenteTratamientoDTO save(AntecedenteTratamientoDTO antecedenteTratamientoDTO, int idPaciente) {
        log.debug("Guardando antecedente de tratamiento para paciente con ID: {}", idPaciente);
        
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + idPaciente));
                
        AntecedenteTratamiento antecedenteTratamiento = antecedenteTratamientoMapper.toEntity(antecedenteTratamientoDTO);
        antecedenteTratamiento.setPaciente(paciente);
        
        // Guardar el antecedente de tratamiento
        AntecedenteTratamiento savedAntecedente = antecedentesRepo.save(antecedenteTratamiento);
        
        return antecedenteTratamientoMapper.toDto(savedAntecedente);
    }
    
    /**
     * Actualiza un antecedente de tratamiento existente.
     *
     * @param id El ID del antecedente de tratamiento a actualizar
     * @param antecedenteTratamientoDTO Los nuevos datos del antecedente de tratamiento
     * @return El DTO del antecedente de tratamiento actualizado
     * @throws ResourceNotFoundException si el antecedente de tratamiento no existe
     */
    @Transactional
    @CacheEvict(value = {"antecedenteTratamiento", "antecedenteTratamientos", "pacienteAntecedenteTratamientos"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.canEditAntecedenteTratamiento(#id, authentication.name)")
    public AntecedenteTratamientoDTO update(int id, AntecedenteTratamientoDTO antecedenteTratamientoDTO) {
        log.debug("Actualizando antecedente de tratamiento con ID: {}", id);
        
        AntecedenteTratamiento existingAntecedente = antecedentesRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Antecedente de tratamiento no encontrado con ID: " + id));
        
        // Conservar el id_paciente original
        Paciente paciente = existingAntecedente.getPaciente();
        
        AntecedenteTratamiento antecedenteTratamiento = antecedenteTratamientoMapper.toEntity(antecedenteTratamientoDTO);
        antecedenteTratamiento.setId(id);
        antecedenteTratamiento.setPaciente(paciente);
        
        AntecedenteTratamiento updatedAntecedente = antecedentesRepo.save(antecedenteTratamiento);
        
        return antecedenteTratamientoMapper.toDto(updatedAntecedente);
    }
    
    /**
     * Elimina un antecedente de tratamiento por su ID.
     *
     * @param id El ID del antecedente de tratamiento a eliminar
     * @throws ResourceNotFoundException si el antecedente de tratamiento no existe
     */
    @Transactional
    @CacheEvict(value = {"antecedenteTratamiento", "antecedenteTratamientos", "pacienteAntecedenteTratamientos"}, allEntries = true)
    @PreAuthorize("hasRole('NUTRICIONISTA') and @securityService.canEditAntecedenteTratamiento(#id, authentication.name)")
    public void deleteById(int id) {
        log.debug("Eliminando antecedente de tratamiento con ID: {}", id);
        
        if (!antecedentesRepo.existsById(id)) {
            throw new ResourceNotFoundException("Antecedente de tratamiento no encontrado con ID: " + id);
        }
        
        antecedentesRepo.deleteById(id);
    }
    
    /**
     * Encuentra todos los antecedentes de tratamiento de un paciente.
     *
     * @param idPaciente El ID del paciente
     * @return Lista de antecedentes de tratamiento como DTOs
     * @throws ResourceNotFoundException si el paciente no existe
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "pacienteAntecedenteTratamientos", key = "#idPaciente")
    @PreAuthorize("hasRole('NUTRICIONISTA') or @securityService.isPacienteAssignedToNutricionista(#idPaciente, authentication.name) or @securityService.isPacienteOwner(#idPaciente, authentication.name)")
    public List<AntecedenteTratamientoDTO> findByPatientId(int idPaciente) {
        log.debug("Buscando antecedentes de tratamiento para paciente con ID: {}", idPaciente);
        
        if (!pacienteRepository.existsById(idPaciente)) {
            throw new ResourceNotFoundException("Paciente no encontrado con ID: " + idPaciente);
        }
        
        return antecedentesRepo.findByPacienteId(idPaciente).stream()
                .map(antecedenteTratamientoMapper::toDto)
                .collect(Collectors.toList());
    }
}
