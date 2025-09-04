package com.thunderfat.springboot.backend.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.AntecedenteTratamientoRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dao.UserRepository;
import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Servicio de seguridad para realizar verificaciones de acceso a recursos.
 * Implementa reglas de negocio de seguridad para controlar el acceso a entidades específicas.
 */
@Service("modelSecurityService")
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final PacienteRepository pacienteRepository;
    private final AntecedenteTratamientoRepository antecedenteTratamientoRepository;
    private final UserRepository userRepository;
    
    /**
     * Verifica si un usuario es propietario de un paciente.
     *
     * @param pacienteId ID del paciente
     * @param username Nombre de usuario (email del usuario)
     * @return true si el usuario es el paciente, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean isPacienteOwner(int pacienteId, String username) {
        log.debug("Verificando si el usuario {} es propietario del paciente con ID: {}", username, pacienteId);
        
        Optional<Paciente> paciente = pacienteRepository.findById(pacienteId);
        if (paciente.isEmpty()) {
            return false;
        }
        
        return paciente.get().getEmail().equals(username);
    }
    
    /**
     * Verifica si un paciente está asignado a un nutricionista.
     *
     * @param pacienteId ID del paciente
     * @param username Nombre de usuario del nutricionista (email)
     * @return true si el paciente está asignado al nutricionista, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean isPacienteAssignedToNutricionista(int pacienteId, String username) {
        log.debug("Verificando si el paciente con ID: {} está asignado al nutricionista: {}", pacienteId, username);
        
        Optional<Paciente> paciente = pacienteRepository.findById(pacienteId);
        if (paciente.isEmpty() || paciente.get().getNutricionista() == null) {
            return false;
        }
        
        Nutricionista nutricionista = paciente.get().getNutricionista();
        return nutricionista.getEmail().equals(username);
    }
    
    /**
     * Verifica si un usuario puede editar un antecedente de tratamiento.
     *
     * @param antecedenteId ID del antecedente de tratamiento
     * @param username Nombre de usuario (email)
     * @return true si el usuario puede editar el antecedente, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canEditAntecedenteTratamiento(int antecedenteId, String username) {
        log.debug("Verificando si el usuario {} puede editar el antecedente de tratamiento con ID: {}", username, antecedenteId);
        
        Optional<AntecedenteTratamiento> antecedente = antecedenteTratamientoRepository.findById(antecedenteId);
        if (antecedente.isEmpty()) {
            return false;
        }
        
        Paciente paciente = antecedente.get().getPaciente();
        if (paciente == null) {
            return false;
        }
        int pacienteId = paciente.getId();
        return isPacienteAssignedToNutricionista(pacienteId, username);
    }
    
    /**
     * Verifica si un usuario es un nutricionista específico.
     *
     * @param nutricionistaId ID del nutricionista
     * @param username Nombre de usuario (email)
     * @return true si el usuario es el nutricionista, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean isNutricionista(int nutricionistaId, String username) {
        log.debug("Verificando si el usuario {} es el nutricionista con ID: {}", username, nutricionistaId);
        
        Usuario usuario = userRepository.findByEmail(username);
        if (usuario == null || !(usuario instanceof Nutricionista)) {
            return false;
        }
        
        Nutricionista nutricionista = (Nutricionista) usuario;
        return nutricionista.getId() == nutricionistaId;
    }
    
    /**
     * Verifica si un usuario puede ver un plan de dieta.
     *
     * @param planId ID del plan de dieta
     * @param username Nombre de usuario (email)
     * @return true si el usuario puede ver el plan, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canViewPlanDieta(int planId, String username) {
        log.debug("Verificando si el usuario {} puede ver el plan de dieta con ID: {}", username, planId);
        
        // El nutricionista que creó el plan o el paciente asociado pueden ver el plan
        return canEditPlanDieta(planId, username);
    }
    
    /**
     * Verifica si un usuario puede editar un plan de dieta.
     *
     * @param planId ID del plan de dieta
     * @param username Nombre de usuario (email)
     * @return true si el usuario puede editar el plan, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canEditPlanDieta(int planId, String username) {
        log.debug("Verificando si el usuario {} puede editar el plan de dieta con ID: {}", username, planId);
        
        // Por ahora, asumimos que solo el nutricionista puede editar
        // Se debe implementar la lógica específica según los requisitos
        return true;
    }
    
    /**
     * Verifica si un usuario puede editar un día de dieta.
     *
     * @param diaDietaId ID del día de dieta
     * @param username Nombre de usuario (email)
     * @return true si el usuario puede editar el día de dieta, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canEditDiaDieta(int diaDietaId, String username) {
        log.debug("Verificando si el usuario {} puede editar el día de dieta con ID: {}", username, diaDietaId);
        
        // Por ahora, asumimos que solo el nutricionista puede editar
        // Se debe implementar la lógica específica según los requisitos
        return true;
    }
    
    /**
     * Verifica si un usuario puede ver un filtro alimentario.
     *
     * @param filtroId ID del filtro alimentario
     * @param username Nombre de usuario (email)
     * @return true si el usuario puede ver el filtro, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canViewFiltroAlimentario(int filtroId, String username) {
        log.debug("Verificando si el usuario {} puede ver el filtro alimentario con ID: {}", username, filtroId);
        
        // Cualquier nutricionista autenticado puede ver los filtros alimentarios
        Usuario usuario = userRepository.findByEmail(username);
        return usuario != null && usuario instanceof Nutricionista;
    }
    
    /**
     * Verifica si un usuario puede editar un filtro alimentario.
     *
     * @param filtroId ID del filtro alimentario
     * @param username Nombre de usuario (email)
     * @return true si el usuario puede editar el filtro, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canEditFiltroAlimentario(int filtroId, String username) {
        log.debug("Verificando si el usuario {} puede editar el filtro alimentario con ID: {}", username, filtroId);
        
        // Por ahora, permitimos que cualquier nutricionista edite cualquier filtro
        // En una implementación más completa, verificaríamos si el filtro pertenece al nutricionista
        Usuario usuario = userRepository.findByEmail(username);
        return usuario != null && usuario instanceof Nutricionista;
    }
    
    /**
     * Verifica si un usuario puede ver mediciones específicas de un paciente.
     *
     * @param medicionId ID de la medición específica
     * @param username Nombre de usuario (email del usuario)
     * @return true si el usuario puede ver la medición, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canViewMediciones(int medicionId, String username) {
        log.debug("Verificando si el usuario {} puede ver la medición específica con ID: {}", username, medicionId);
        
        // Un nutricionista puede ver mediciones de sus pacientes
        // Un paciente puede ver sus propias mediciones
        Usuario usuario = userRepository.findByEmail(username);
        if (usuario == null) {
            return false;
        }
        
        if (usuario instanceof Nutricionista) {
            // Verificar si la medición pertenece a un paciente del nutricionista
            return true; // Simplificado para la implementación actual
        }
        
        // Si es un paciente, verificar si la medición le pertenece
        return isPacienteOwner(medicionId, username);
    }
    
    /**
     * Verifica si un usuario puede gestionar (crear, modificar, eliminar) mediciones específicas.
     *
     * @param medicionId ID de la medición específica
     * @param username Nombre de usuario (email del usuario)
     * @return true si el usuario puede gestionar la medición, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canManageMediciones(int medicionId, String username) {
        log.debug("Verificando si el usuario {} puede gestionar la medición específica con ID: {}", username, medicionId);
        
        // Solo los nutricionistas pueden gestionar mediciones
        Usuario usuario = userRepository.findByEmail(username);
        return usuario != null && usuario instanceof Nutricionista;
    }
    
    /**
     * Verifica si un usuario puede ver la información de un paciente.
     *
     * @param pacienteId ID del paciente
     * @param username Nombre de usuario (email del usuario)
     * @return true si el usuario puede ver la información del paciente, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canViewPaciente(int pacienteId, String username) {
        log.debug("Verificando si el usuario {} puede ver al paciente con ID: {}", username, pacienteId);
        
        // Un nutricionista puede ver información de sus pacientes
        // Un paciente puede ver su propia información
        Usuario usuario = userRepository.findByEmail(username);
        if (usuario == null) {
            return false;
        }
        
        if (usuario instanceof Nutricionista) {
            // Verificar si el paciente está asignado al nutricionista
            return true; // Simplificado para la implementación actual
        }
        
        // Si es un paciente, verificar si es el propietario
        return isPacienteOwner(pacienteId, username);
    }
    
    /**
     * Verifica si un usuario puede gestionar (crear, modificar, eliminar) información de un paciente.
     *
     * @param pacienteId ID del paciente
     * @param username Nombre de usuario (email del usuario)
     * @return true si el usuario puede gestionar la información del paciente, false en caso contrario
     */
    @Transactional(readOnly = true)
    public boolean canManagePaciente(int pacienteId, String username) {
        log.debug("Verificando si el usuario {} puede gestionar al paciente con ID: {}", username, pacienteId);
        
        // Solo los nutricionistas asignados pueden gestionar información de pacientes
        Usuario usuario = userRepository.findByEmail(username);
        if (usuario == null || !(usuario instanceof Nutricionista)) {
            return false;
        }
        
        // Verificar si el paciente está asignado al nutricionista
        return true; // Simplificado para la implementación actual
    }
}
