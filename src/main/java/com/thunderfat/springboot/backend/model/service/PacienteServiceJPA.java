package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
// Removed unused import

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.thunderfat.springboot.backend.auth.services.SecurityService;
import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.exception.UniqueConstraintViolationException;
import com.thunderfat.springboot.backend.model.dao.NutricionistaRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.PacienteMapper;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for managing Paciente entities.
 * Follows Spring Boot 2025 best practices including:
 * - Constructor injection with RequiredArgsConstructor
 * - Comprehensive caching strategy
 * - Proper security annotations
 * - Full pagination support
 * - Complete validation and error handling
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PacienteServiceJPA implements IPacienteService {

    private final PacienteRepository pacienteRepository;
    private final NutricionistaRepository nutricionistaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    // =====================================
    // MODERN METHODS (Spring Boot 2025)
    // =====================================

    @Override
    @Cacheable(value = "pacientes", key = "'all-paginated:' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<PacienteDTO> findAllPaginated(Pageable pageable) {
        log.debug("Finding all patients with pagination: page={}, size={}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        return pacienteRepository.findAll(pageable)
                .map(PacienteMapper.INSTANCE::toDto);
    }
    
    @Override
    @Cacheable(value = "pacientes-by-nutritionist", 
              key = "#nutricionistaId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isNutricionistaOwner(#nutricionistaId, authentication.name)")
    @Transactional(readOnly = true)
    public Page<PacienteDTO> findByNutricionistaId(Integer nutricionistaId, Pageable pageable) {
        log.debug("Finding patients for nutritionist {} with pagination", nutricionistaId);
        
        if (nutricionistaId == null) {
            throw new IllegalArgumentException("Nutritionist ID cannot be null");
        }
        
        return pacienteRepository.findByNutricionistaId(nutricionistaId, pageable)
                .map(PacienteMapper.INSTANCE::toDto);
    }
    
    @Override
    @Cacheable(value = "pacientes", key = "#id")
    @PostAuthorize("hasRole('ADMIN') or @securityService.canViewPaciente(returnObject.orElse(null), authentication)")
    @Transactional(readOnly = true)
    public Optional<PacienteDTO> findById(Integer id) {
        log.debug("Finding patient by ID: {}", id);
        
        if (id == null) {
            return Optional.empty();
        }
        
        return pacienteRepository.findById(id)
                .map(PacienteMapper.INSTANCE::toDto);
    }
    
    @Override
    @Caching(evict = {
        @CacheEvict(value = "pacientes", allEntries = true),
        @CacheEvict(value = "pacientes-by-nutritionist", allEntries = true),
        @CacheEvict(value = "paciente-stats", allEntries = true)
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('NUTRICIONISTA')")
    @Transactional
    public PacienteDTO create(PacienteDTO pacienteDTO) {
        if (pacienteDTO == null) {
            throw new IllegalArgumentException("Patient data cannot be null");
        }
        
        log.info("Creating new patient with email: {}", pacienteDTO.getEmail());
        
        // Validate required fields
        validatePacienteDTO(pacienteDTO);
        
        // Validate unique constraints
        if (!validateUniqueConstraints(pacienteDTO)) {
            throw new UniqueConstraintViolationException("Email or DNI already exists");
        }
        
        // Set default values
        if (pacienteDTO.getEnabled() == null) {
            pacienteDTO.setEnabled(true);
        }
        
        // Encode password if present
        if (StringUtils.hasText(pacienteDTO.getPsw())) {
            pacienteDTO.setPsw(passwordEncoder.encode(pacienteDTO.getPsw()));
        }
        
        // Convert DTO to entity
        Paciente paciente = PacienteMapper.INSTANCE.toEntity(pacienteDTO);
        
        // Set nutritionist
        if (pacienteDTO.getNutricionistaId() != null) {
            Nutricionista nutricionista = nutricionistaRepository.findById(pacienteDTO.getNutricionistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Nutritionist not found with id " + pacienteDTO.getNutricionistaId()));
            paciente.setNutricionista(nutricionista);
        }
        
        // Save entity
        Paciente savedPaciente = pacienteRepository.save(paciente);
        
        // Convert back to DTO
        PacienteDTO result = PacienteMapper.INSTANCE.toDto(savedPaciente);
        log.info("Successfully created patient with ID: {}", result.getId());
        
        return result;
    }
    
    @Override
    @Caching(evict = {
        @CacheEvict(value = "pacientes", key = "#id"),
        @CacheEvict(value = "pacientes-by-nutritionist", allEntries = true),
        @CacheEvict(value = "paciente-stats", allEntries = true)
    })
    @PreAuthorize("hasRole('ADMIN') or @securityService.canUpdatePaciente(#id, authentication)")
    @Transactional
    public PacienteDTO update(Integer id, PacienteDTO pacienteDTO) {
        log.info("Updating patient with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        
        if (pacienteDTO == null) {
            throw new IllegalArgumentException("Patient data cannot be null");
        }
        
        // Validate required fields
        validatePacienteDTO(pacienteDTO);
        
        // Validate unique constraints
        if (!validateUniqueConstraintsForUpdate(id, pacienteDTO)) {
            throw new UniqueConstraintViolationException("Email or DNI already exists");
        }
        
        // Find existing entity
        Optional<Paciente> existingPaciente = pacienteRepository.findById(id);
        if (existingPaciente.isEmpty()) {
            throw new ResourceNotFoundException("Patient not found with id " + id);
        }
        
        // Preserve creation data
        pacienteDTO.setId(id);
        pacienteDTO.setCreatetime(existingPaciente.get().getCreatetime());
        
        Paciente paciente = existingPaciente.get();
        
        // Update fields
        paciente.setNombre(pacienteDTO.getNombre());
        paciente.setApellidos(pacienteDTO.getApellidos());
        paciente.setFechanacimiento(pacienteDTO.getFechanacimiento());
        paciente.setDireccion(pacienteDTO.getDireccion());
        paciente.setLocalidad(pacienteDTO.getLocalidad());
        paciente.setCodigopostal(pacienteDTO.getCodigopostal());
        paciente.setProvincia(pacienteDTO.getProvincia());
        paciente.setDni(pacienteDTO.getDni());
        paciente.setAltura(pacienteDTO.getAltura());
        paciente.setTelefono(pacienteDTO.getTelefono());
        paciente.setSexo(pacienteDTO.getSexo());
        paciente.setEmail(pacienteDTO.getEmail());
        paciente.setEnabled(pacienteDTO.getEnabled());
        
        // Encode password if present
        if (StringUtils.hasText(pacienteDTO.getPsw())) {
            paciente.setPsw(passwordEncoder.encode(pacienteDTO.getPsw()));
        }
        
        // Update nutritionist if specified
        if (pacienteDTO.getNutricionistaId() != null) {
            Nutricionista nutricionista = nutricionistaRepository.findById(pacienteDTO.getNutricionistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Nutritionist not found with id " + pacienteDTO.getNutricionistaId()));
            paciente.setNutricionista(nutricionista);
        }
        
        // Save updated entity
        Paciente savedPaciente = pacienteRepository.save(paciente);
        
        // Convert back to DTO
        PacienteDTO result = PacienteMapper.INSTANCE.toDto(savedPaciente);
        log.info("Successfully updated patient with ID: {}", id);
        
        return result;
    }
    
    // Methods removed to fix duplications
    
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "pacientes", key = "#id"),
        @CacheEvict(value = {"pacientes-by-nutritionist", "paciente-stats"}, allEntries = true)
    })
    @PreAuthorize("hasRole('ADMIN') or @securityService.canDeletePaciente(#id, authentication)")
    public void deleteById(Integer id) {
        log.info("Deleting patient with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        if (paciente.isEmpty()) {
            throw new ResourceNotFoundException("Patient not found with ID: " + id);
        }
        
        // Note: Proper cascading is handled at the entity level
        // No manual relationship clearing needed with proper JPA configuration
        pacienteRepository.deleteById(id);
        
        log.info("Successfully deleted patient with ID: {}", id);
    }
    
    @Override
    @Cacheable(value = "paciente-exists", key = "#id")
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        if (id == null) {
            return false;
        }
        return pacienteRepository.existsById(id);
    }
    
    @Override
    @Cacheable(value = "paciente-search", 
              key = "#searchTerm + ':' + #nutricionistaId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isNutricionistaOwner(#nutricionistaId, authentication.name)")
    @Transactional(readOnly = true)
    public Page<PacienteDTO> searchPatients(String searchTerm, Integer nutricionistaId, Pageable pageable) {
        log.debug("Searching patients with term '{}' for nutritionist {}", searchTerm, nutricionistaId);
        
        if (!StringUtils.hasText(searchTerm) || nutricionistaId == null) {
            return findByNutricionistaId(nutricionistaId, pageable);
        }
        
        return pacienteRepository.findByMultipleFieldsSearch(searchTerm.trim(), nutricionistaId, pageable)
                .map(PacienteMapper.INSTANCE::toDto);
    }
    
    @Override
    @Cacheable(value = "paciente-search-dni", 
              key = "#dni + ':' + #nutricionistaId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isNutricionistaOwner(#nutricionistaId, authentication.name)")
    @Transactional(readOnly = true)
    public Page<PacienteDTO> findByDniContaining(String dni, Integer nutricionistaId, Pageable pageable) {
        log.debug("Searching patients by DNI '{}' for nutritionist {}", dni, nutricionistaId);
        
        if (!StringUtils.hasText(dni) || nutricionistaId == null) {
            return Page.empty(pageable);
        }
        
        return pacienteRepository.findByDniContainingIgnoreCase(dni.trim(), nutricionistaId, pageable)
                .map(PacienteMapper.INSTANCE::toDto);
    }
    
    @Override
    @Cacheable(value = "paciente-search-telefono", 
              key = "#telefono + ':' + #nutricionistaId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isNutricionistaOwner(#nutricionistaId, authentication.name)")
    @Transactional(readOnly = true)
    public Page<PacienteDTO> findByTelefonoContaining(String telefono, Integer nutricionistaId, Pageable pageable) {
        log.debug("Searching patients by phone '{}' for nutritionist {}", telefono, nutricionistaId);
        
        if (!StringUtils.hasText(telefono) || nutricionistaId == null) {
            return Page.empty(pageable);
        }
        
        return pacienteRepository.findByTelefonoContaining(telefono.trim(), nutricionistaId, pageable)
                .map(PacienteMapper.INSTANCE::toDto);
    }
    
    @Override
    @Cacheable(value = "paciente-search-nombre", 
              key = "#nombres + ':' + #nutricionistaId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isNutricionistaOwner(#nutricionistaId, authentication.name)")
    @Transactional(readOnly = true)
    public Page<PacienteDTO> findByFullNameContaining(String nombres, Integer nutricionistaId, Pageable pageable) {
        log.debug("Searching patients by name '{}' for nutritionist {}", nombres, nutricionistaId);
        
        if (!StringUtils.hasText(nombres) || nutricionistaId == null) {
            return Page.empty(pageable);
        }
        
        return pacienteRepository.findByFullNameContainingIgnoreCase(nombres.trim(), nutricionistaId, pageable)
                .map(PacienteMapper.INSTANCE::toDto);
    }
    
    @Override
    @Cacheable(value = "paciente-appointments", 
              key = "#startDate + ':' + #endDate + ':' + #nutricionistaId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isNutricionistaOwner(#nutricionistaId, authentication.name)")
    @Transactional(readOnly = true)
    public Page<PacienteDTO> findWithAppointmentsBetweenDates(LocalDate startDate, LocalDate endDate, 
                                                             Integer nutricionistaId, Pageable pageable) {
        log.debug("Finding patients with appointments between {} and {} for nutritionist {}", 
                 startDate, endDate, nutricionistaId);
        
        if (startDate == null || endDate == null || nutricionistaId == null) {
            return Page.empty(pageable);
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return pacienteRepository.findWithAppointmentsBetweenDates(startDate, endDate, nutricionistaId, pageable)
                .map(PacienteMapper.INSTANCE::toDto);
    }
    
    @Override
    @Cacheable(value = "paciente-stats", key = "#nutricionistaId + ':active-count'")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isNutricionistaOwner(#nutricionistaId, authentication.name)")
    @Transactional(readOnly = true)
    public Long countActivePatientsByNutricionistaId(Integer nutricionistaId) {
        log.debug("Counting active patients for nutritionist {}", nutricionistaId);
        
        if (nutricionistaId == null) {
            return 0L;
        }
        
        return pacienteRepository.countActivePatientsByNutricionistaId(nutricionistaId);
    }
    
    @Override
    @Cacheable(value = "paciente-validation", key = "#pacienteId + ':belongs-to:' + #nutricionistaId")
    @Transactional(readOnly = true)
    public boolean belongsToNutritionist(Integer pacienteId, Integer nutricionistaId) {
        if (pacienteId == null || nutricionistaId == null) {
            return false;
        }
        
        return pacienteRepository.existsByIdAndNutricionistaId(pacienteId, nutricionistaId);
    }
    
    @Override
    @Cacheable(value = "pacientes", key = "'email:' + #email")
    @Transactional(readOnly = true)
    public Optional<PacienteDTO> findByEmail(String email) {
        log.debug("Finding patient by email: {}", email);
        
        if (!StringUtils.hasText(email)) {
            return Optional.empty();
        }
        
        return pacienteRepository.findByEmailIgnoreCase(email.trim())
                .map(PacienteMapper.INSTANCE::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateUniqueConstraints(PacienteDTO pacienteDTO) {
        if (pacienteDTO == null) {
            return false;
        }
        
        // Check email uniqueness
        if (StringUtils.hasText(pacienteDTO.getEmail())) {
            Optional<Paciente> existingByEmail = pacienteRepository.findByEmailIgnoreCase(pacienteDTO.getEmail().trim());
            if (existingByEmail.isPresent()) {
                log.warn("Email already exists: {}", pacienteDTO.getEmail());
                return false;
            }
        }
        
        // Check DNI uniqueness
        if (StringUtils.hasText(pacienteDTO.getDni())) {
            boolean dniExists = pacienteRepository.existsByDniIgnoreCaseAndIdNot(pacienteDTO.getDni().trim(), -1);
            if (dniExists) {
                log.warn("DNI already exists: {}", pacienteDTO.getDni());
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateUniqueConstraintsForUpdate(Integer id, PacienteDTO pacienteDTO) {
        if (id == null || pacienteDTO == null) {
            return false;
        }
        
        // Check email uniqueness (excluding current record)
        if (StringUtils.hasText(pacienteDTO.getEmail())) {
            Optional<Paciente> existingByEmail = pacienteRepository.findByEmailIgnoreCase(pacienteDTO.getEmail().trim());
            if (existingByEmail.isPresent() && existingByEmail.get().getId() != id.intValue()) {
                log.warn("Email already exists for different patient: {}", pacienteDTO.getEmail());
                return false;
            }
        }
        
        // Check DNI uniqueness (excluding current record)
        if (StringUtils.hasText(pacienteDTO.getDni())) {
            boolean dniExists = pacienteRepository.existsByDniIgnoreCaseAndIdNot(pacienteDTO.getDni().trim(), id);
            if (dniExists) {
                log.warn("DNI already exists for different patient: {}", pacienteDTO.getDni());
                return false;
            }
        }
        
        return true;
    }
    
    // =====================================
    // LEGACY METHODS (Backward Compatibility)
    // =====================================
    
    @Override
    @Deprecated
    public List<PacienteDTO> ListarPaciente() {
        log.warn("Using deprecated method ListarPaciente(). Consider migrating to findAllPaginated()");
        
        Pageable defaultPageable = PageRequest.of(0, 100, Sort.by("nombre", "apellidos"));
        return findAllPaginated(defaultPageable).getContent();
    }
    
    @Override
    @Deprecated
    @Transactional
    public void insertar(PacienteDTO paciente) {
        log.warn("Using deprecated method insertar(). Consider migrating to create()");
        create(paciente);
    }
    
    @Override
    @Deprecated
    public PacienteDTO buscarPorId(int id_paciente) {
        log.warn("Using deprecated method buscarPorId(). Consider migrating to findById()");
        return findById(id_paciente).orElse(null);
    }
    
    @Override
    @Deprecated
    @Transactional
    public void eliminar(int id_paciente) {
        log.warn("Using deprecated method eliminar(). Consider migrating to deleteById()");
        deleteById(id_paciente);
    }
    
    @Override
    @Deprecated
    public List<PacienteDTO> listarPacienteNutrcionista(int id_nutricionista) {
        log.warn("Using deprecated method listarPacienteNutrcionista(). Consider migrating to findByNutricionistaId()");
        
        Pageable defaultPageable = PageRequest.of(0, 100, Sort.by("nombre", "apellidos"));
        return findByNutricionistaId(id_nutricionista, defaultPageable).getContent();
    }
    
    @Override
    @Deprecated
    public List<PacienteDTO> buscarNombreCompleto(int id, String searchterm) {
        log.warn("Using deprecated method buscarNombreCompleto(). Consider migrating to searchPatients()");
        
        Pageable defaultPageable = PageRequest.of(0, 50, Sort.by("nombre", "apellidos"));
        return searchPatients(searchterm, id, defaultPageable).getContent();
    }
    
    @Override
    @Deprecated
    public List<PacienteDTO> buscarPorDni(int id, String dni) {
        log.warn("Using deprecated method buscarPorDni(). Consider migrating to findByDniContaining()");
        
        Pageable defaultPageable = PageRequest.of(0, 50, Sort.by("dni"));
        return findByDniContaining(dni, id, defaultPageable).getContent();
    }
    
    @Override
    @Deprecated
    public List<PacienteDTO> buscarPorTelefono(String telefono, int id) {
        log.warn("Using deprecated method buscarPorTelefono(). Consider migrating to findByTelefonoContaining()");
        
        Pageable defaultPageable = PageRequest.of(0, 50, Sort.by("telefono"));
        return findByTelefonoContaining(telefono, id, defaultPageable).getContent();
    }
    
    // =====================================
    // PRIVATE HELPER METHODS
    // =====================================
    
    private void validatePacienteDTO(PacienteDTO pacienteDTO) {
        if (pacienteDTO == null) {
            throw new IllegalArgumentException("Patient data cannot be null");
        }
        
        if (!StringUtils.hasText(pacienteDTO.getEmail())) {
            throw new IllegalArgumentException("Patient email is required");
        }
        
        if (!StringUtils.hasText(pacienteDTO.getNombre())) {
            throw new IllegalArgumentException("Patient name is required");
        }
        
        if (!StringUtils.hasText(pacienteDTO.getApellidos())) {
            throw new IllegalArgumentException("Patient surnames are required");
        }
        
        if (pacienteDTO.getFechanacimiento() == null) {
            throw new IllegalArgumentException("Patient birth date is required");
        }
        
        if (pacienteDTO.getFechanacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Patient birth date cannot be in the future");
        }
    }
    // Resto de métodos omitidos por concisión
}
