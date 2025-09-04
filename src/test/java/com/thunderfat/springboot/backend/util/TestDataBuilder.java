package com.thunderfat.springboot.backend.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;
import com.thunderfat.springboot.backend.model.dto.CitaDTO;
import com.thunderfat.springboot.backend.model.dto.ComidaDTO;
import com.thunderfat.springboot.backend.model.dto.NutricionistaDTO;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.PlanDietaDTO;
import com.thunderfat.springboot.backend.model.entity.Alimento;
import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.entity.Rol;

/**
 * Test data builder for creating consistent test objects.
 * Provides factory methods for creating valid test data.
 * 
 * @author ThunderFat Test Team
 * @since Spring Boot 3.5.4
 */
public class TestDataBuilder {

    private static final String TEST_PASSWORD = "$2a$04$test.password.hash";
    
    // ====================================
    // NUTRICIONISTA TEST DATA
    // ====================================
    
    public static NutricionistaDTO buildValidNutricionistaDTO() {
        return NutricionistaDTO.builder()
                .id(1)
                .nombre("Dr. Test")
                .apellidos("Nutrition Specialist")
                .email("test.nutritionist@example.com")
                .telefono("123456789")
                .numeroColegiadoProfesional("COL12345")
                .dni("12345678A")
                .enabled(true)
                .build();
    }
    
    public static Nutricionista buildValidNutricionista() {
        Nutricionista nutricionista = new Nutricionista();
        nutricionista.setId(1);
        nutricionista.setNombre("Dr. Test");
        nutricionista.setApellidos("Nutrition Specialist");
        nutricionista.setEmail("test.nutritionist@example.com");
        nutricionista.setTelefono("123456789");
        nutricionista.setNumeroColegiadoProfesional("COL12345");
        nutricionista.setDni("12345678A");
        nutricionista.setPsw(TEST_PASSWORD);
        nutricionista.setEnabled(true);
        
        // Add role
        Rol role = new Rol();
        role.setId(1);
        role.setNombre("ROLE_NUTRICIONISTA");
        List<Rol> roles = new ArrayList<>();
        roles.add(role);
        nutricionista.setRoles(roles);
        
        return nutricionista;
    }
    
    // ====================================
    // PACIENTE TEST DATA
    // ====================================
    
    public static PacienteDTO buildValidPacienteDTO(Integer nutricionistaId) {
        return PacienteDTO.builder()
                .id(1)
                .nombre("John")
                .apellidos("Test Patient")
                .email("john.patient@example.com")
                .telefono("987654321")
                .dni("12345678A")
                .fechanacimiento(LocalDate.of(1990, 1, 1))
                .nutricionistaId(nutricionistaId)
                .build();
    }
    
    public static Paciente buildValidPaciente(Nutricionista nutricionista) {
        Paciente paciente = new Paciente();
        paciente.setId(1);
        paciente.setNombre("John");
        paciente.setApellidos("Test Patient");
        paciente.setEmail("john.patient@example.com");
        paciente.setTelefono("987654321");
        paciente.setDni("12345678A");
        paciente.setFechanacimiento(LocalDate.of(1990, 1, 1));
        paciente.setNutricionista(nutricionista);
        paciente.setPsw(TEST_PASSWORD);
        paciente.setEnabled(true);
        
        // Add role
        Rol role = new Rol();
        role.setId(2);
        role.setNombre("ROLE_PACIENTE");
        List<Rol> roles = new ArrayList<>();
        roles.add(role);
        paciente.setRoles(roles);
        
        return paciente;
    }
    
    // ====================================
    // PLAN DIETA TEST DATA
    // ====================================
    
    public static PlanDietaDTO buildValidPlanDietaDTO(Integer nutricionistaId, Integer pacienteId) {
        return PlanDietaDTO.builder()
                .id(1)
                .fechaini(LocalDate.now())
                .fechafin(LocalDate.now().plusDays(30))
                .idNutricionista(nutricionistaId)
                .idPaciente(pacienteId)
                .visible(true)
                .intercambiable(false)
                .build();
    }
    
    public static PlanDieta buildValidPlanDieta(Nutricionista nutricionista, Paciente paciente) {
        PlanDieta plan = new PlanDieta();
        plan.setId(1);
        plan.setFechaini(LocalDate.now());
        plan.setFechafin(LocalDate.now().plusDays(30));
        plan.setId_nutricionista(nutricionista.getId());
        plan.setId_paciente(paciente.getId());
        plan.setVisible((short) 1);
        plan.setIntercambiable((short) 0);
        plan.setCalrangomin(1500.0);
        plan.setCalrangomax(2000.0);
        return plan;
    }
    
    // ====================================
    // CITA TEST DATA
    // ====================================
    
    public static CitaDTO buildValidCitaDTO(Integer nutricionistaId, Integer pacienteId) {
        return CitaDTO.builder()
                .id(1)
                .fechaInicio(LocalDateTime.now().plusDays(1))
                .fechaFin(LocalDateTime.now().plusDays(1).plusHours(1))
                .notas("Test appointment")
                .nutricionistaId(nutricionistaId)
                .pacienteId(pacienteId)
                .build();
    }
    
    public static Cita buildValidCita(Nutricionista nutricionista, Paciente paciente) {
        Cita cita = new Cita();
        cita.setId(1);
        cita.setFechaini(LocalDateTime.now().plusDays(1));
        cita.setFechafin(LocalDateTime.now().plusDays(1).plusHours(1));
        cita.setNutricionista(nutricionista);
        cita.setPaciente(paciente);
        return cita;
    }
    
    // ====================================
    // ALIMENTO TEST DATA
    // ====================================
    
    public static AlimentoDTO buildValidAlimentoDTO() {
        return AlimentoDTO.builder()
                .id(1)
                .nombre("Test Food")
                .estado("Active")
                .cal(100.0)
                .hidratosdecarbono(20.0)
                .proteinas(5.0)
                .grasas(2.0)
                .h2o(70.0)
                .build();
    }
    
    public static Alimento buildValidAlimento() {
        Alimento alimento = new Alimento();
        alimento.setId(1);
        alimento.setNombre("Test Food");
        alimento.setEstado("Active");
        alimento.setCal(100.0);
        alimento.setHidratosdecarbono(20.0);
        alimento.setProteinas(5.0);
        alimento.setGrasas(2.0);
        alimento.setH2o(70.0);
        return alimento;
    }
    
    // ====================================
    // COMIDA TEST DATA
    // ====================================
    
    public static ComidaDTO buildValidComidaDTO() {
        ComidaDTO comida = new ComidaDTO();
        comida.setId(1);
        comida.setHora(LocalTime.of(8, 0));
        comida.setValoracion(5);
        return comida;
    }
    
    public static Comida buildValidComida() {
        Comida comida = new Comida();
        comida.setId(1);
        comida.setHora(LocalTime.of(8, 0));
        comida.setValoracion(5);
        return comida;
    }
    
    // ====================================
    // OVERLAPPING TEST DATA
    // ====================================
    
    public static PlanDietaDTO buildOverlappingPlanDietaDTO(Integer nutricionistaId, Integer pacienteId) {
        return PlanDietaDTO.builder()
                .fechaini(LocalDate.now().minusDays(5))
                .fechafin(LocalDate.now().plusDays(5))
                .idNutricionista(nutricionistaId)
                .idPaciente(pacienteId)
                .visible(true)
                .intercambiable(false)
                .build();
    }
    
    public static CitaDTO buildConflictingCitaDTO(Integer nutricionistaId, Integer pacienteId, LocalDateTime existingTime) {
        return CitaDTO.builder()
                .fechaInicio(existingTime.plusMinutes(30)) // Overlaps with existing appointment
                .fechaFin(existingTime.plusMinutes(90))
                .notas("Should conflict with existing appointment")
                .nutricionistaId(nutricionistaId)
                .pacienteId(pacienteId)
                .build();
    }
    
    // ====================================
    // INVALID TEST DATA
    // ====================================
    
    public static PacienteDTO buildInvalidPacienteDTO() {
        return PacienteDTO.builder()
                .nombre("") // Invalid: empty name
                .email("invalid-email") // Invalid: bad email format
                .dni("123") // Invalid: short DNI
                .telefono("") // Invalid: empty phone
                .build();
    }
    
    public static CitaDTO buildInvalidCitaDTO() {
        return CitaDTO.builder()
                .fechaInicio(LocalDateTime.now().minusDays(1)) // Invalid: past date
                .fechaFin(LocalDateTime.now().minusDays(1).plusMinutes(300)) // Invalid: too long duration
                .notas("") // Invalid: empty reason
                .build();
    }
}
