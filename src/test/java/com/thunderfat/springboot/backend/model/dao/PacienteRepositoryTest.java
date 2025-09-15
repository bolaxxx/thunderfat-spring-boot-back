package com.thunderfat.springboot.backend.model.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.thunderfat.springboot.backend.config.TestDataJpaConfig;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

/**
 * Integration tests for PacienteRepository.
 * Demonstrates Spring Boot 2025 testing best practices including:
 * - @DataJpaTest for repository layer testing
 * - TestEntityManager for test data setup
 * - Comprehensive test coverage for all repository methods
 * - Performance testing for pagination
 * - Test profiles for different environments
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@DataJpaTest
@Import(TestDataJpaConfig.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:pacientetest;DB_CLOSE_DELAY=-1;CASE_INSENSITIVE_IDENTIFIERS=TRUE"
})
@ActiveProfiles("test")
@DisplayName("Paciente Repository Tests")
class PacienteRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    private Nutricionista testNutricionista;
    private Paciente testPaciente1;
    private Paciente testPaciente2;
    
    @BeforeEach
    void setUp() {
        // Create test nutritionist
        testNutricionista = new Nutricionista();
        testNutricionista.setEmail("test.nutricionista@thunderfat.com");
        testNutricionista.setNombre("Dr. Test");
        testNutricionista.setApellidos("Nutricionista");
        testNutricionista.setEnabled(true);
        testNutricionista = entityManager.persistAndFlush(testNutricionista);
        
        // Create test patients
        testPaciente1 = createTestPaciente(
            "12345678A", "Juan", "Pérez García", 
            "juan.perez@example.com", "600123456", "Madrid"
        );
        
        testPaciente2 = createTestPaciente(
            "87654321B", "María", "González López", 
            "maria.gonzalez@example.com", "600654321", "Barcelona"
        );
        
        entityManager.persistAndFlush(testPaciente1);
        entityManager.persistAndFlush(testPaciente2);
    }
    
    private Paciente createTestPaciente(String dni, String nombre, String apellidos, 
                                       String email, String telefono, String localidad) {
        Paciente paciente = new Paciente();
        paciente.setDni(dni);
        paciente.setNombre(nombre);
        paciente.setApellidos(apellidos);
        paciente.setEmail(email);
        paciente.setTelefono(telefono);
        paciente.setLocalidad(localidad);
        paciente.setFechanacimiento(LocalDate.of(1990, 1, 1));
        paciente.setNutricionista(testNutricionista);
        paciente.setEnabled(true);
        return paciente;
    }
    
    @Test
    @DisplayName("Should find patients by nutritionist ID with pagination")
    void testFindByNutricionistaIdWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Paciente> result = pacienteRepository.findByNutricionistaId(
            testNutricionista.getId(), pageable
        );
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        
        // Verify all patients belong to the correct nutritionist
        result.getContent().forEach(paciente -> 
            assertThat(paciente.getNutricionista().getId()).isEqualTo(testNutricionista.getId())
        );
    }
    
    @Test
    @DisplayName("Should find patients by nutritionist ID without pagination")
    void testFindByNutricionistaIdWithoutPagination() {
        // When
        List<Paciente> result = pacienteRepository.findByNutricionistaId(testNutricionista.getId());
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("nombre").containsExactlyInAnyOrder("Juan", "María");
    }
    
    @Test
    @DisplayName("Should find patients by DNI containing search term")
    void testFindByDniContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Paciente> result = pacienteRepository.findByDniContainingIgnoreCase(
            "1234", testNutricionista.getId(), pageable
        );
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDni()).isEqualTo("12345678A");
    }
    
    @Test
    @DisplayName("Should find patients by phone number containing search term")
    void testFindByTelefonoContaining() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Paciente> result = pacienteRepository.findByTelefonoContaining(
            "600123", testNutricionista.getId(), pageable
        );
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTelefono()).isEqualTo("600123456");
    }
    
    @Test
    @DisplayName("Should find patients by full name search")
    void testFindByFullNameContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When - Search by first name
        Page<Paciente> result1 = pacienteRepository.findByFullNameContainingIgnoreCase(
            "juan", testNutricionista.getId(), pageable
        );
        
        // When - Search by last name
        Page<Paciente> result2 = pacienteRepository.findByFullNameContainingIgnoreCase(
            "gonzález", testNutricionista.getId(), pageable
        );
        
        // When - Search by full name
        Page<Paciente> result3 = pacienteRepository.findByFullNameContainingIgnoreCase(
            "maría gonzález", testNutricionista.getId(), pageable
        );
        
        // Then
        assertThat(result1.getContent()).hasSize(1);
        assertThat(result1.getContent().get(0).getNombre()).isEqualTo("Juan");
        
        assertThat(result2.getContent()).hasSize(1);
        assertThat(result2.getContent().get(0).getApellidos()).contains("González");
        
        assertThat(result3.getContent()).hasSize(1);
        assertThat(result3.getContent().get(0).getNombre()).isEqualTo("María");
    }
    
    @Test
    @DisplayName("Should perform multi-field search")
    void testFindByMultipleFieldsSearch() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When - Search term that matches name
        Page<Paciente> result1 = pacienteRepository.findByMultipleFieldsSearch(
            "juan", testNutricionista.getId(), pageable
        );
        
        // When - Search term that matches DNI
        Page<Paciente> result2 = pacienteRepository.findByMultipleFieldsSearch(
            "1234", testNutricionista.getId(), pageable
        );
        
        // When - Search term that matches email
        Page<Paciente> result3 = pacienteRepository.findByMultipleFieldsSearch(
            "maria.gonzalez", testNutricionista.getId(), pageable
        );
        
        // Then
        assertThat(result1.getContent()).hasSize(1);
        assertThat(result2.getContent()).hasSize(1);
        assertThat(result3.getContent()).hasSize(1);
    }
    
    @Test
    @DisplayName("Should find patient by email ignoring case")
    void testFindByEmailIgnoreCase() {
        // When
        Optional<Paciente> result = pacienteRepository.findByEmailIgnoreCase("JUAN.PEREZ@EXAMPLE.COM");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("juan.perez@example.com");
    }
    
    @Test
    @DisplayName("Should check if patient exists for nutritionist")
    void testExistsByIdAndNutricionistaId() {
        // When
        boolean exists = pacienteRepository.existsByIdAndNutricionistaId(
            testPaciente1.getId(), testNutricionista.getId()
        );
        
        boolean notExists = pacienteRepository.existsByIdAndNutricionistaId(
            testPaciente1.getId(), 999
        );
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
    
    @Test
    @DisplayName("Should count active patients for nutritionist")
    void testCountActivePatientsByNutricionistaId() {
        // When
        Long count = pacienteRepository.countActivePatientsByNutricionistaId(testNutricionista.getId());
        
        // Then
        assertThat(count).isEqualTo(2L);
    }
    
    @Test
    @DisplayName("Should find patients by locality and nutritionist")
    void testFindByLocalidadIgnoreCaseAndNutricionistaId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Paciente> result = pacienteRepository.findByLocalidadIgnoreCaseAndNutricionistaId(
            "madrid", testNutricionista.getId(), pageable
        );
        
        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLocalidad()).isEqualTo("Madrid");
    }
    
    @Test
    @DisplayName("Should check email uniqueness excluding specific ID")
    void testExistsByEmailIgnoreCaseAndIdNot() {
        // When
        boolean existsForOther = pacienteRepository.existsByEmailIgnoreCaseAndIdNot(
            "juan.perez@example.com", testPaciente2.getId()
        );
        
        boolean notExistsForSame = pacienteRepository.existsByEmailIgnoreCaseAndIdNot(
            "juan.perez@example.com", testPaciente1.getId()
        );
        
        // Then
        assertThat(existsForOther).isTrue(); // Email exists for another patient
        assertThat(notExistsForSame).isFalse(); // Email doesn't exist for other patients (excluding self)
    }
    
    @Test
    @DisplayName("Should handle pagination correctly")
    void testPaginationBehavior() {
        // Create additional test patients
        for (int i = 3; i <= 15; i++) {
            String dniNumber = String.format("%08d", 12345600 + i); // Ensures exactly 8 digits
            Paciente paciente = createTestPaciente(
                dniNumber + "X", "Paciente" + i, "Apellido" + i,
                "paciente" + i + "@example.com", "60012345" + i, "Ciudad" + i
            );
            entityManager.persistAndFlush(paciente);
        }
        
        // Given - Request first page with 5 elements
        Pageable pageable = PageRequest.of(0, 5);
        
        // When
        Page<Paciente> firstPage = pacienteRepository.findByNutricionistaId(
            testNutricionista.getId(), pageable
        );
        
        // Then
        assertThat(firstPage.getContent()).hasSize(5);
        assertThat(firstPage.getTotalElements()).isEqualTo(15); // 2 original + 13 new
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
        assertThat(firstPage.isFirst()).isTrue();
        assertThat(firstPage.isLast()).isFalse();
        assertThat(firstPage.hasNext()).isTrue();
    }
    
    @Test
    @DisplayName("Should return empty results for non-existent nutritionist")
    void testFindByNonExistentNutritionist() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<Paciente> result = pacienteRepository.findByNutricionistaId(999, pageable);
        
        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}
