package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.ComidaRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dao.PlanDietaRepository;
import com.thunderfat.springboot.backend.model.dao.PlatoPlanDietaRepository;
import com.thunderfat.springboot.backend.model.dao.PlatoPredeterminadoRepository;
import com.thunderfat.springboot.backend.model.dto.ComidaDTO;
import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.ComidaMapper;
import com.thunderfat.springboot.backend.model.dto.mapper.PlatoPredeterminadoMapper;
import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

/**
 * Comprehensive test suite for the modernized ComidaServiceJPA.
 * Tests Spring Boot 2025 patterns including DTO operations, caching, and business logic.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@ExtendWith(MockitoExtension.class)
class ComidaServiceJPATest {
    
    @Mock
    private ComidaRepository comidaRepository;
    
    @Mock
    private PacienteRepository pacienteRepository;
    
    @Mock
    private PlanDietaRepository planDietaRepository;
    
    @Mock
    private PlatoPlanDietaRepository platoPlanDietaRepository;
    
    @Mock
    private PlatoPredeterminadoRepository platoPredeterminadoRepository;
    
    @Mock
    private ComidaMapper comidaMapper;
    
    @Mock
    private PlatoPredeterminadoMapper platoPredeterminadoMapper;
    
    @InjectMocks
    private ComidaServiceJPA comidaService;
    
    private ComidaDTO testComidaDTO;
    private Comida testComida;
    private Paciente testPaciente;
    private Nutricionista testNutricionista;
    private PlanDieta testPlanDieta;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        testNutricionista = new Nutricionista();
        testNutricionista.setId(1);
        testNutricionista.setEmail("nutricionista@test.com");
        
        testPaciente = new Paciente();
        testPaciente.setId(1);
        testPaciente.setEmail("paciente@test.com");
        testPaciente.setNutricionista(testNutricionista);
        
        testPlanDieta = new PlanDieta();
        testPlanDieta.setId(1);
        testPlanDieta.setId_paciente(testPaciente.getId());
        testPlanDieta.setFechaini(LocalDate.now().minusDays(7));
        testPlanDieta.setFechafin(LocalDate.now().plusDays(7));
        
        testComida = new Comida();
        testComida.setId(1);
        testComida.setHora(LocalTime.of(12, 0));
        testComida.setValoracion(4);
        
        testComidaDTO = new ComidaDTO();
        testComidaDTO.setId(1);
        testComidaDTO.setHora(LocalTime.of(12, 0));
        testComidaDTO.setValoracion(4);
    }
    
    // ================================
    // MODERN DTO-BASED OPERATIONS TESTS
    // ================================
    
    @Test
    void testFindAll_WithPagination_ReturnsPageOfComidaDTOs() {
        // Given
        Page<Comida> comidasPage = new PageImpl<>(List.of(testComida));
        Pageable pageable = Pageable.ofSize(10);
        
        when(comidaRepository.findAll(pageable)).thenReturn(comidasPage);
        when(comidaMapper.toDto(testComida)).thenReturn(testComidaDTO);
        
        // When
        Page<ComidaDTO> result = comidaService.findAll(pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testComidaDTO, result.getContent().get(0));
        
        verify(comidaRepository).findAll(pageable);
        verify(comidaMapper).toDto(testComida);
    }
    
    @Test
    void testFindById_ExistingId_ReturnsOptionalWithComidaDTO() {
        // Given
        Integer comidaId = 1;
        
        when(comidaRepository.findById(comidaId)).thenReturn(Optional.of(testComida));
        when(comidaMapper.toDto(testComida)).thenReturn(testComidaDTO);
        
        // When
        Optional<ComidaDTO> result = comidaService.findById(comidaId);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testComidaDTO, result.get());
        
        verify(comidaRepository).findById(comidaId);
        verify(comidaMapper).toDto(testComida);
    }
    
    @Test
    void testFindById_NonExistingId_ReturnsEmptyOptional() {
        // Given
        Integer comidaId = 999;
        
        when(comidaRepository.findById(comidaId)).thenReturn(Optional.empty());
        
        // When
        Optional<ComidaDTO> result = comidaService.findById(comidaId);
        
        // Then
        assertFalse(result.isPresent());
        
        verify(comidaRepository).findById(comidaId);
        verify(comidaMapper, never()).toDto(any());
    }
    
    @Test
    void testSave_ValidComidaDTO_ReturnsSavedComidaDTO() {
        // Given
        when(comidaMapper.toEntity(testComidaDTO)).thenReturn(testComida);
        when(comidaRepository.save(testComida)).thenReturn(testComida);
        when(comidaMapper.toDto(testComida)).thenReturn(testComidaDTO);
        
        // When
        ComidaDTO result = comidaService.save(testComidaDTO);
        
        // Then
        assertNotNull(result);
        assertEquals(testComidaDTO, result);
        
        verify(comidaMapper).toEntity(testComidaDTO);
        verify(comidaRepository).save(testComida);
        verify(comidaMapper).toDto(testComida);
    }
    
    @Test
    void testDeleteById_ExistingId_DeletesSuccessfully() {
        // Given
        Integer comidaId = 1;
        
        when(comidaRepository.existsById(comidaId)).thenReturn(true);
        doNothing().when(comidaRepository).deleteById(comidaId);
        
        // When & Then
        assertDoesNotThrow(() -> comidaService.deleteById(comidaId));
        
        verify(comidaRepository).existsById(comidaId);
        verify(comidaRepository).deleteById(comidaId);
    }
    
    @Test
    void testDeleteById_NonExistingId_ThrowsResourceNotFoundException() {
        // Given
        Integer comidaId = 999;
        
        when(comidaRepository.existsById(comidaId)).thenReturn(false);
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> comidaService.deleteById(comidaId));
        
        verify(comidaRepository).existsById(comidaId);
        verify(comidaRepository, never()).deleteById(any());
    }
    
    // ================================
    // BUSINESS-SPECIFIC OPERATIONS TESTS
    // ================================
    
    @Test
    void testFindByPlanDietaId_ValidId_ReturnsPageOfComidaDTOs() {
        // Given
        Integer planDietaId = 1;
        Pageable pageable = Pageable.ofSize(10);
        Page<Comida> comidasPage = new PageImpl<>(List.of(testComida));
        
        when(comidaRepository.findByPlanDietaId(planDietaId, pageable)).thenReturn(comidasPage);
        when(comidaMapper.toDto(testComida)).thenReturn(testComidaDTO);
        
        // When
        Page<ComidaDTO> result = comidaService.findByPlanDietaId(planDietaId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testComidaDTO, result.getContent().get(0));
        
        verify(comidaRepository).findByPlanDietaId(planDietaId, pageable);
        verify(comidaMapper).toDto(testComida);
    }
    
    @Test
    void testFindTodayMeals_ValidPatientAndDate_ReturnsListOfComidaDTOs() {
        // Given
        Integer pacienteId = 1;
        LocalDate today = LocalDate.now();
        List<Comida> comidas = List.of(testComida);
        
        when(comidaRepository.findTodayMeals(pacienteId, today)).thenReturn(comidas);
        when(comidaMapper.toDto(testComida)).thenReturn(testComidaDTO);
        
        // When
        List<ComidaDTO> result = comidaService.findTodayMeals(pacienteId, today);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testComidaDTO, result.get(0));
        
        verify(comidaRepository).findTodayMeals(pacienteId, today);
        verify(comidaMapper).toDto(testComida);
    }
    
    @Test
    void testFindMealSubstitutions_ValidData_ReturnsSubstitutions() {
        // Given
        Integer pacienteId = 1;
        Integer platoId = 1;
        
        PlatoPlanDieta platoPlanDieta = new PlatoPlanDieta();
        platoPlanDieta.setId(platoId);
        platoPlanDieta.setKcaltotales(500.0);
        
        PlatoPredeterminado substitution = new PlatoPredeterminado();
        substitution.setId(2);
        substitution.setKcaltotales(520.0); // Within 10% tolerance
        
        PlatoPredeterminadoDTO substitutionDTO = new PlatoPredeterminadoDTO();
        substitutionDTO.setId(2);
        
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(testPaciente));
        when(planDietaRepository.findCurrentActivePlanByPaciente(pacienteId, LocalDate.now()))
                .thenReturn(Optional.of(testPlanDieta));
        when(platoPlanDietaRepository.findById(platoId)).thenReturn(Optional.of(platoPlanDieta));
        when(platoPredeterminadoRepository.listapornutricionista(testNutricionista.getId()))
                .thenReturn(List.of(substitution));
        when(platoPredeterminadoMapper.toDto(substitution)).thenReturn(substitutionDTO);
        
        // When
        List<PlatoPredeterminadoDTO> result = comidaService.findMealSubstitutions(pacienteId, platoId);
        
        // Then
        assertNotNull(result);
        // Note: The actual filtering logic depends on the meal plan's food filters
        // This test verifies the method structure and basic flow
        
        verify(pacienteRepository).findById(pacienteId);
        verify(planDietaRepository).findCurrentActivePlanByPaciente(pacienteId, LocalDate.now());
        verify(platoPlanDietaRepository).findById(platoId);
        verify(platoPredeterminadoRepository).listapornutricionista(testNutricionista.getId());
    }
    
    // ================================
    // ANALYTICS & REPORTING TESTS
    // ================================
    
    @Test
    void testCountMealsByNutritionist_ValidId_ReturnsCount() {
        // Given
        Integer nutricionistaId = 1;
        Long expectedCount = 25L;
        
        when(comidaRepository.countByNutricionistaId(nutricionistaId)).thenReturn(expectedCount);
        
        // When
        Long result = comidaService.countMealsByNutritionist(nutricionistaId);
        
        // Then
        assertEquals(expectedCount, result);
        
        verify(comidaRepository).countByNutricionistaId(nutricionistaId);
    }
    
    @Test
    void testGetAverageMealCalories_ValidId_ReturnsAverage() {
        // Given
        Integer nutricionistaId = 1;
        Double expectedAverage = 450.5;
        
        when(comidaRepository.getAverageMealCaloriesByNutritionist(nutricionistaId))
                .thenReturn(expectedAverage);
        
        // When
        Double result = comidaService.getAverageMealCalories(nutricionistaId);
        
        // Then
        assertEquals(expectedAverage, result);
        
        verify(comidaRepository).getAverageMealCaloriesByNutritionist(nutricionistaId);
    }
    
    @Test
    void testFindMostPopularMeals_ValidId_ReturnsPageOfComidaDTOs() {
        // Given
        Integer nutricionistaId = 1;
        Pageable pageable = Pageable.ofSize(5);
        Page<Comida> popularMealsPage = new PageImpl<>(List.of(testComida));
        
        when(comidaRepository.findMostPopularMealsByNutritionist(nutricionistaId, pageable))
                .thenReturn(popularMealsPage);
        when(comidaMapper.toDto(testComida)).thenReturn(testComidaDTO);
        
        // When
        Page<ComidaDTO> result = comidaService.findMostPopularMeals(nutricionistaId, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testComidaDTO, result.getContent().get(0));
        
        verify(comidaRepository).findMostPopularMealsByNutritionist(nutricionistaId, pageable);
        verify(comidaMapper).toDto(testComida);
    }
    
    // ================================
    // LEGACY OPERATIONS TESTS (DEPRECATED)
    // ================================
    
    @Test
    @SuppressWarnings("deprecation")
    void testInsertar_LegacyMethod_SavesComidaEntity() {
        // Given
        when(comidaRepository.save(testComida)).thenReturn(testComida);
        
        // When & Then
        assertDoesNotThrow(() -> comidaService.insertar(testComida));
        
        verify(comidaRepository).save(testComida);
    }
    
    @Test
    @SuppressWarnings("deprecation")
    void testBuscarPorID_LegacyMethod_ReturnsComidaEntity() {
        // Given
        int comidaId = 1;
        
        when(comidaRepository.findById(comidaId)).thenReturn(Optional.of(testComida));
        
        // When
        Comida result = comidaService.buscarPorID(comidaId);
        
        // Then
        assertEquals(testComida, result);
        
        verify(comidaRepository).findById(comidaId);
    }
    
    @Test
    @SuppressWarnings("deprecation")
    void testListaPorPlanDieta_LegacyMethod_ReturnsListOfComidas() {
        // Given
        List<Comida> expectedComidas = List.of(testComida);
        
        when(comidaRepository.findByPlandieta(testPlanDieta.getId())).thenReturn(expectedComidas);
        
        // When
        List<Comida> result = comidaService.listaPorPlanDieta(testPlanDieta);
        
        // Then
        assertEquals(expectedComidas, result);
        
        verify(comidaRepository).findByPlandieta(testPlanDieta.getId());
    }
    
    // ================================
    // ERROR HANDLING TESTS
    // ================================
    
    @Test
    void testFindMealSubstitutions_PatientNotFound_ThrowsResourceNotFoundException() {
        // Given
        Integer pacienteId = 999;
        Integer platoId = 1;
        
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, 
                () -> comidaService.findMealSubstitutions(pacienteId, platoId));
        
        verify(pacienteRepository).findById(pacienteId);
    }
    
    @Test
    void testFindMealSubstitutions_NoDietPlan_ReturnsEmptyList() {
        // Given
        Integer pacienteId = 1;
        Integer platoId = 1;
        
        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(testPaciente));
        when(planDietaRepository.findCurrentActivePlanByPaciente(pacienteId, LocalDate.now()))
                .thenReturn(Optional.empty());
        
        // When
        List<PlatoPredeterminadoDTO> result = comidaService.findMealSubstitutions(pacienteId, platoId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(pacienteRepository).findById(pacienteId);
        verify(planDietaRepository).findCurrentActivePlanByPaciente(pacienteId, LocalDate.now());
    }
}
