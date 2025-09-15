package com.thunderfat.springboot.backend.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dao.NutricionistaRepository;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.PacienteMapper;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.exception.UniqueConstraintViolationException;

/**
 * Comprehensive test suite for PacienteServiceJPA.
 * Tests cover all modern Spring Boot 2025 patterns and backward compatibility.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PacienteServiceJPA Tests")
class PacienteServiceJPATest {

    @Mock
    private PacienteRepository pacienteRepository;
    
    @Mock
    private NutricionistaRepository nutricionistaRepository;
    
    @Mock
    private PacienteMapper pacienteMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private PacienteServiceJPA pacienteService;
    
    private PacienteDTO testPacienteDTO;
    private Paciente testPaciente;
    private Pageable defaultPageable;
    
    @BeforeEach
    void setUp() {
        defaultPageable = PageRequest.of(0, 10);
        
        // Create test data
        testPacienteDTO = PacienteDTO.builder()
                .id(1)
                .nombre("Juan")
                .apellidos("Pérez García")
                .email("juan.perez@example.com")
                .dni("12345678A")
                .telefono("600123456")
                .fechanacimiento(LocalDate.of(1990, 1, 15))
                .sexo("MASCULINO")
                .altura(175.0)
                .enabled(true)
                .nutricionistaId(1)
                .psw("password123")
                .build();
        
        testPaciente = new Paciente();
        testPaciente.setId(1);
        testPaciente.setNombre("Juan");
        testPaciente.setApellidos("Pérez García");
        testPaciente.setEmail("juan.perez@example.com");
        testPaciente.setDni("12345678A");
        testPaciente.setTelefono("600123456");
        testPaciente.setFechanacimiento(LocalDate.of(1990, 1, 15));
        testPaciente.setSexo("MASCULINO");
        testPaciente.setEnabled(true);
        testPaciente.setCreatetime(LocalDateTime.now());
        testPaciente.setPsw("encodedPassword");
    }

    @Nested
    @DisplayName("Modern Methods Tests")
    class ModernMethodsTests {

        @Test
        @DisplayName("Should find all patients with pagination")
        void shouldFindAllPatientsWithPagination() {
            // Given
            Page<Paciente> expectedPage = new PageImpl<>(List.of(testPaciente));
            when(pacienteRepository.findAll(defaultPageable)).thenReturn(expectedPage);

            // When
            Page<PacienteDTO> result = pacienteService.findAllPaginated(defaultPageable);

            // Then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).hasSize(1),
                () -> assertThat(result.getContent().get(0).getNombre()).isEqualTo("Juan"),
                () -> verify(pacienteRepository).findAll(defaultPageable)
            );
        }

        @Test
        @DisplayName("Should find patients by nutritionist ID with pagination")
        void shouldFindPatientsByNutricionistaIdWithPagination() {
            // Given
            Integer nutricionistaId = 100;
            Page<Paciente> expectedPage = new PageImpl<>(List.of(testPaciente));
            when(pacienteRepository.findByNutricionistaId(nutricionistaId, defaultPageable))
                    .thenReturn(expectedPage);

            // When
            Page<PacienteDTO> result = pacienteService.findByNutricionistaId(nutricionistaId, defaultPageable);

            // Then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).hasSize(1),
                () -> assertThat(result.getContent().get(0).getNombre()).isEqualTo("Juan"),
                () -> verify(pacienteRepository).findByNutricionistaId(nutricionistaId, defaultPageable)
            );
        }

        @Test
        @DisplayName("Should throw exception when nutritionist ID is null")
        void shouldThrowExceptionWhenNutricionistaIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> pacienteService.findByNutricionistaId(null, defaultPageable))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Nutritionist ID cannot be null");
        }

        @Test
        @DisplayName("Should find patient by ID")
        void shouldFindPatientById() {
            // Given
            when(pacienteRepository.findById(1)).thenReturn(Optional.of(testPaciente));

            // When
            Optional<PacienteDTO> result = pacienteService.findById(1);

            // Then
            assertAll(
                () -> assertThat(result).isPresent(),
                () -> assertThat(result.get().getNombre()).isEqualTo("Juan"),
                () -> assertThat(result.get().getEmail()).isEqualTo("juan.perez@example.com"),
                () -> verify(pacienteRepository).findById(1)
            );
        }

        @Test
        @DisplayName("Should return empty when patient ID is null")
        void shouldReturnEmptyWhenPatientIdIsNull() {
            // When
            Optional<PacienteDTO> result = pacienteService.findById(null);

            // Then
            assertThat(result).isEmpty();
            verify(pacienteRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should create new patient successfully")
        void shouldCreateNewPatientSuccessfully() {
            // Given
            Nutricionista testNutricionista = new Nutricionista();
            testNutricionista.setId(1);
            
            when(pacienteRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
            when(pacienteRepository.existsByDniIgnoreCaseAndIdNot(anyString(), eq(-1))).thenReturn(false);
            when(nutricionistaRepository.findById(1)).thenReturn(Optional.of(testNutricionista));
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(pacienteRepository.save(any(Paciente.class))).thenReturn(testPaciente);

            // When
            PacienteDTO result = pacienteService.create(testPacienteDTO);

            // Then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getNombre()).isEqualTo("Juan"),
                () -> assertThat(result.getEnabled()).isTrue(),
                () -> verify(passwordEncoder).encode("password123"),
                () -> verify(pacienteRepository).save(any(Paciente.class))
            );
        }

        @Test
        @DisplayName("Should throw exception when creating patient with existing email")
        void shouldThrowExceptionWhenCreatingPatientWithExistingEmail() {
            // Given
            when(pacienteRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testPaciente));

            // When & Then
            assertThatThrownBy(() -> pacienteService.create(testPacienteDTO))
                    .isInstanceOf(UniqueConstraintViolationException.class)
                    .hasMessage("Email or DNI already exists");
        }

        @Test
        @DisplayName("Should update existing patient successfully")
        void shouldUpdateExistingPatientSuccessfully() {
            // Given
            Integer patientId = 1;
            
            // Mock nutricionista
            Nutricionista nutricionista = new Nutricionista();
            nutricionista.setId(1);
            nutricionista.setNombre("Dr. Test");
            nutricionista.setApellidos("Nutritionist");
            nutricionista.setEmail("dr.test@example.com");
            when(nutricionistaRepository.findById(1)).thenReturn(Optional.of(nutricionista));
            
            when(pacienteRepository.findById(patientId)).thenReturn(Optional.of(testPaciente));
            when(pacienteRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testPaciente));
            when(pacienteRepository.existsByDniIgnoreCaseAndIdNot(anyString(), eq(patientId))).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
            when(pacienteRepository.save(any(Paciente.class))).thenReturn(testPaciente);

            testPacienteDTO.setNombre("Juan Updated");

            // When
            PacienteDTO result = pacienteService.update(patientId, testPacienteDTO);

            // Then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(patientId),
                () -> verify(pacienteRepository).findById(patientId),
                () -> verify(pacienteRepository).save(any(Paciente.class))
            );
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent patient")
        void shouldThrowExceptionWhenUpdatingNonExistentPatient() {
            // Given
            Integer patientId = 999;
            when(pacienteRepository.findById(patientId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> pacienteService.update(patientId, testPacienteDTO))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Patient not found with id " + patientId);
        }

        @Test
        @DisplayName("Should delete patient successfully")
        void shouldDeletePatientSuccessfully() {
            // Given
            Integer patientId = 1;
            when(pacienteRepository.findById(patientId)).thenReturn(Optional.of(testPaciente));

            // When
            pacienteService.deleteById(patientId);

            // Then
            verify(pacienteRepository).deleteById(patientId);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent patient")
        void shouldThrowExceptionWhenDeletingNonExistentPatient() {
            // Given
            Integer patientId = 999;
            when(pacienteRepository.findById(patientId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> pacienteService.deleteById(patientId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Patient not found with ID: " + patientId);
        }

        @Test
        @DisplayName("Should check if patient exists")
        void shouldCheckIfPatientExists() {
            // Given
            when(pacienteRepository.existsById(1)).thenReturn(true);
            when(pacienteRepository.existsById(999)).thenReturn(false);

            // When & Then
            assertAll(
                () -> assertTrue(pacienteService.existsById(1)),
                () -> assertFalse(pacienteService.existsById(999)),
                () -> assertFalse(pacienteService.existsById(null))
            );
        }

        @Test
        @DisplayName("Should search patients with multiple fields")
        void shouldSearchPatientsWithMultipleFields() {
            // Given
            String searchTerm = "Juan";
            Integer nutricionistaId = 100;
            Page<Paciente> expectedPage = new PageImpl<>(List.of(testPaciente));
            when(pacienteRepository.findByMultipleFieldsSearch(searchTerm, nutricionistaId, defaultPageable))
                    .thenReturn(expectedPage);

            // When
            Page<PacienteDTO> result = pacienteService.searchPatients(searchTerm, nutricionistaId, defaultPageable);

            // Then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).hasSize(1),
                () -> verify(pacienteRepository).findByMultipleFieldsSearch(searchTerm, nutricionistaId, defaultPageable)
            );
        }

        @Test
        @DisplayName("Should find patients by DNI containing")
        void shouldFindPatientsByDniContaining() {
            // Given
            String dni = "12345";
            Integer nutricionistaId = 100;
            Page<Paciente> expectedPage = new PageImpl<>(List.of(testPaciente));
            when(pacienteRepository.findByDniContainingIgnoreCase(dni, nutricionistaId, defaultPageable))
                    .thenReturn(expectedPage);

            // When
            Page<PacienteDTO> result = pacienteService.findByDniContaining(dni, nutricionistaId, defaultPageable);

            // Then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).hasSize(1),
                () -> verify(pacienteRepository).findByDniContainingIgnoreCase(dni, nutricionistaId, defaultPageable)
            );
        }

        @Test
        @DisplayName("Should validate unique constraints successfully")
        void shouldValidateUniqueConstraintsSuccessfully() {
            // Given
            when(pacienteRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
            when(pacienteRepository.existsByDniIgnoreCaseAndIdNot(anyString(), eq(-1))).thenReturn(false);

            // When
            boolean result = pacienteService.validateUniqueConstraints(testPacienteDTO);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("Should fail unique constraints validation for existing email")
        void shouldFailUniqueConstraintsValidationForExistingEmail() {
            // Given
            when(pacienteRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(testPaciente));

            // When
            boolean result = pacienteService.validateUniqueConstraints(testPacienteDTO);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Legacy Methods Tests")
    @SuppressWarnings("deprecation")
    class LegacyMethodsTests {

        @Test
        @DisplayName("Should list all patients using legacy method")
        void shouldListAllPatientsUsingLegacyMethod() {
            // Given
            Page<Paciente> expectedPage = new PageImpl<>(List.of(testPaciente));
            when(pacienteRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

            // When
            List<PacienteDTO> result = pacienteService.ListarPaciente();

            // Then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getNombre()).isEqualTo("Juan")
            );
        }

        @Test
        @DisplayName("Should insert patient using legacy method")
        void shouldInsertPatientUsingLegacyMethod() {
            // Given
            Nutricionista testNutricionista = new Nutricionista();
            testNutricionista.setId(1);
            
            when(pacienteRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());
            when(pacienteRepository.existsByDniIgnoreCaseAndIdNot(anyString(), eq(-1))).thenReturn(false);
            when(nutricionistaRepository.findById(1)).thenReturn(Optional.of(testNutricionista));
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(pacienteRepository.save(any(Paciente.class))).thenReturn(testPaciente);

            // When
            pacienteService.insertar(testPacienteDTO);

            // Then
            verify(pacienteRepository).save(any(Paciente.class));
        }

        @Test
        @DisplayName("Should find patient by ID using legacy method")
        void shouldFindPatientByIdUsingLegacyMethod() {
            // Given
            when(pacienteRepository.findById(1)).thenReturn(Optional.of(testPaciente));

            // When
            PacienteDTO result = pacienteService.buscarPorId(1);

            // Then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getNombre()).isEqualTo("Juan"),
                () -> verify(pacienteRepository).findById(1)
            );
        }

        @Test
        @DisplayName("Should delete patient using legacy method")
        void shouldDeletePatientUsingLegacyMethod() {
            // Given
            when(pacienteRepository.findById(1)).thenReturn(Optional.of(testPaciente));

            // When
            pacienteService.eliminar(1);

            // Then
            verify(pacienteRepository).deleteById(1);
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when patient DTO is null")
        void shouldThrowExceptionWhenPatientDtoIsNull() {
            // When & Then
            assertThatThrownBy(() -> pacienteService.create(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Patient data cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when email is missing")
        void shouldThrowExceptionWhenEmailIsMissing() {
            // Given
            testPacienteDTO.setEmail(null);

            // When & Then
            assertThatThrownBy(() -> pacienteService.create(testPacienteDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Patient email is required");
        }

        @Test
        @DisplayName("Should throw exception when name is missing")
        void shouldThrowExceptionWhenNameIsMissing() {
            // Given
            testPacienteDTO.setNombre("");

            // When & Then
            assertThatThrownBy(() -> pacienteService.create(testPacienteDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Patient name is required");
        }

        @Test
        @DisplayName("Should throw exception when birth date is in future")
        void shouldThrowExceptionWhenBirthDateIsInFuture() {
            // Given
            testPacienteDTO.setFechanacimiento(LocalDate.now().plusDays(1));

            // When & Then
            assertThatThrownBy(() -> pacienteService.create(testPacienteDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Patient birth date cannot be in the future");
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should count active patients for nutritionist")
        void shouldCountActivePatientsForNutritionist() {
            // Given
            Integer nutricionistaId = 100;
            when(pacienteRepository.countActivePatientsByNutricionistaId(nutricionistaId)).thenReturn(5L);

            // When
            Long result = pacienteService.countActivePatientsByNutricionistaId(nutricionistaId);

            // Then
            assertAll(
                () -> assertEquals(5L, result),
                () -> verify(pacienteRepository).countActivePatientsByNutricionistaId(nutricionistaId)
            );
        }

        @Test
        @DisplayName("Should validate patient belongs to nutritionist")
        void shouldValidatePatientBelongsToNutritionist() {
            // Given
            Integer pacienteId = 1;
            Integer nutricionistaId = 100;
            when(pacienteRepository.existsByIdAndNutricionistaId(pacienteId, nutricionistaId)).thenReturn(true);

            // When
            boolean result = pacienteService.belongsToNutritionist(pacienteId, nutricionistaId);

            // Then
            assertTrue(result);
            verify(pacienteRepository).existsByIdAndNutricionistaId(pacienteId, nutricionistaId);
        }

        @Test
        @DisplayName("Should find patient by email")
        void shouldFindPatientByEmail() {
            // Given
            String email = "juan.perez@example.com";
            when(pacienteRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(testPaciente));

            // When
            Optional<PacienteDTO> result = pacienteService.findByEmail(email);

            // Then
            assertAll(
                () -> assertThat(result).isPresent(),
                () -> assertThat(result.get().getEmail()).isEqualTo(email),
                () -> verify(pacienteRepository).findByEmailIgnoreCase(email)
            );
        }

        @Test
        @DisplayName("Should return empty when email is null or empty")
        void shouldReturnEmptyWhenEmailIsNullOrEmpty() {
            // When & Then
            assertAll(
                () -> assertThat(pacienteService.findByEmail(null)).isEmpty(),
                () -> assertThat(pacienteService.findByEmail("")).isEmpty(),
                () -> assertThat(pacienteService.findByEmail("  ")).isEmpty()
            );
        }
    }
}
