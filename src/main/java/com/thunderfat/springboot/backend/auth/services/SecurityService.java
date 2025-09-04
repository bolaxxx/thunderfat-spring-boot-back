package com.thunderfat.springboot.backend.auth.services;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.NutricionistaRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Security service providing authorization and access control methods.
 * Used for method-level security annotations.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Service("authSecurityService")
@Slf4j
@RequiredArgsConstructor
public class SecurityService {

    private final NutricionistaRepository nutricionistaRepository;
    private final PacienteRepository pacienteRepository;
    
    /**
     * Checks if the authenticated user is the owner of the specified nutritionist ID.
     * 
     * @param nutricionistaId the nutritionist ID to check
     * @param username the authenticated username
     * @return true if the user is the owner
     */
    @Transactional(readOnly = true)
    public boolean isNutricionistaOwner(Integer nutricionistaId, String username) {
        if (nutricionistaId == null || username == null || username.isEmpty()) {
            return false;
        }
        
        // Find nutritionist with matching ID and username
        Optional<Nutricionista> nutricionista = nutricionistaRepository.findById(nutricionistaId);
        return nutricionista.isPresent() && username.equals(nutricionista.get().getEmail());
    }
    
    /**
     * Checks if the authenticated user can view the specified patient.
     * 
     * @param pacienteDTO the patient DTO to check
     * @param authentication the authenticated user
     * @return true if the user can view the patient
     */
    @Transactional(readOnly = true)
    public boolean canViewPaciente(PacienteDTO pacienteDTO, Authentication authentication) {
        if (pacienteDTO == null || authentication == null) {
            return false;
        }
        
        String username = authentication.getName();
        
        // If the authenticated user is the patient
        if (username.equals(pacienteDTO.getEmail())) {
            return true;
        }
        
        // If the authenticated user is the patient's nutritionist
        return isNutricionistaOwner(pacienteDTO.getNutricionistaId(), username);
    }
    
    /**
     * Checks if the authenticated user can update the specified patient.
     * 
     * @param pacienteId the patient ID to check
     * @param authentication the authenticated user
     * @return true if the user can update the patient
     */
    @Transactional(readOnly = true)
    public boolean canUpdatePaciente(Integer pacienteId, Authentication authentication) {
        if (pacienteId == null || authentication == null) {
            return false;
        }
        
        String username = authentication.getName();
        
        // Find the patient
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
        if (pacienteOpt.isEmpty()) {
            return false;
        }
        
        Paciente paciente = pacienteOpt.get();
        
        // If the authenticated user is the patient
        if (username.equals(paciente.getEmail())) {
            return true;
        }
        
        // If the authenticated user is the patient's nutritionist
        return paciente.getNutricionista() != null && 
               isNutricionistaOwner(paciente.getNutricionista().getId(), username);
    }
}
