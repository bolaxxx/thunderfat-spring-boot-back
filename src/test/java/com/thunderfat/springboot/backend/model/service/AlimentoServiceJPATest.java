package com.thunderfat.springboot.backend.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;

import java.util.Arrays;
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

import com.thunderfat.springboot.backend.exception.BusinessException;
import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.AlimentoRepository;
import com.thunderfat.springboot.backend.model.dto.AlimentoDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.AlimentoMapper;
import com.thunderfat.springboot.backend.model.entity.Alimento;

/**
 * Unit tests for AlimentoServiceJPA using Spring Boot 2025 testing best practices
 * 
 * Features tested:
 * - Business logic validation
 * - Exception handling
 * - Caching behavior simulation
 * - Security annotations (via integration tests)
 * - Pagination and sorting
 * - Data mapping with MapStruct
 * 
 * @author ThunderFat Development Team
 * @version 2025.1
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlimentoServiceJPA Unit Tests")
class AlimentoServiceJPATest {
    
    @Mock
    private AlimentoRepository alimentoRepository;
    
    @Mock
    private AlimentoMapper alimentoMapper;
    
    @InjectMocks
    private AlimentoServiceJPA alimentoService;
    
    private Alimento alimento;
    private AlimentoDTO alimentoDTO;
    private Pageable pageable;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        alimento = new Alimento();
        alimento.setId(1);
        alimento.setNombre("Pollo a la plancha");
        alimento.setCal(165.0);
        alimento.setProteinas(25.0);
        alimento.setGrasas(3.6);
        alimento.setHidratosdecarbono(0.0);
        alimento.setEstado("fresco");
        
        alimentoDTO = AlimentoDTO.builder()
            .id(1)
            .nombre("Pollo a la plancha")
            .cal(165.0)
            .proteinas(25.0)
            .grasas(3.6)
            .hidratosdecarbono(0.0)
            .estado("fresco")
            .build();
            
        pageable = PageRequest.of(0, 20);
    }
    
    @Nested
    @DisplayName("Read Operations Tests")
    class ReadOperationsTests {
        
        @Test
        @DisplayName("Should return paginated alimentos successfully")
        void shouldReturnPaginatedAlimentos() {
            // Given
            List<Alimento> alimentos = Arrays.asList(alimento);
            Page<Alimento> alimentosPage = new PageImpl<>(alimentos, pageable, 1);
            
            given(alimentoRepository.findAll(pageable)).willReturn(alimentosPage);
            given(alimentoMapper.toDto(alimento)).willReturn(alimentoDTO);
            
            // When
            Page<AlimentoDTO> result = alimentoService.listarAlimentos(pageable);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getNombre()).isEqualTo("Pollo a la plancha");
            
            then(alimentoRepository).should().findAll(pageable);
            then(alimentoMapper).should().toDto(alimento);
        }
        
        @Test
        @DisplayName("Should find alimento by ID successfully")
        void shouldFindAlimentoById() {
            // Given
            given(alimentoRepository.findById(1)).willReturn(Optional.of(alimento));
            given(alimentoMapper.toDto(alimento)).willReturn(alimentoDTO);
            
            // When
            AlimentoDTO result = alimentoService.buscarPorId(1);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getNombre()).isEqualTo("Pollo a la plancha");
            assertThat(result.getCal()).isEqualTo(165.0);
            
            then(alimentoRepository).should().findById(1);
            then(alimentoMapper).should().toDto(alimento);
        }
        
        @Test
        @DisplayName("Should throw ResourceNotFoundException when alimento not found")
        void shouldThrowExceptionWhenAlimentoNotFound() {
            // Given
            given(alimentoRepository.findById(999)).willReturn(Optional.empty());
            
            // When & Then
            assertThatThrownBy(() -> alimentoService.buscarPorId(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Alimento no encontrado con ID: 999");
            
            then(alimentoRepository).should().findById(999);
            then(alimentoMapper).should(never()).toDto(any());
        }
        
        @Test
        @DisplayName("Should search alimentos by name with pagination")
        void shouldSearchAlimentosByName() {
            // Given
            String searchTerm = "pollo";
            List<Alimento> alimentos = Arrays.asList(alimento);
            Page<Alimento> alimentosPage = new PageImpl<>(alimentos, pageable, 1);
            
            given(alimentoRepository.findByNombreContainingIgnoreCase(searchTerm, pageable))
                .willReturn(alimentosPage);
            given(alimentoMapper.toDto(alimento)).willReturn(alimentoDTO);
            
            // When
            Page<AlimentoDTO> result = alimentoService.buscarPorNombre(searchTerm, pageable);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getNombre()).containsIgnoringCase("pollo");
            
            then(alimentoRepository).should().findByNombreContainingIgnoreCase(searchTerm, pageable);
        }
        
        @Test
        @DisplayName("Should find high protein foods")
        void shouldFindHighProteinFoods() {
            // Given
            Double threshold = 20.0;
            List<Alimento> highProteinAlimentos = Arrays.asList(alimento);
            List<AlimentoDTO> highProteinDTOs = Arrays.asList(alimentoDTO);
            
            given(alimentoRepository.findHighProteinFoods(threshold)).willReturn(highProteinAlimentos);
            given(alimentoMapper.toDtoList(highProteinAlimentos)).willReturn(highProteinDTOs);
            
            // When
            List<AlimentoDTO> result = alimentoService.buscarAlimentosAltoProteina(threshold);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProteinas()).isGreaterThan(threshold);
            
            then(alimentoRepository).should().findHighProteinFoods(threshold);
        }
    }
    
    @Nested
    @DisplayName("Write Operations Tests")
    class WriteOperationsTests {
        
        @Test
        @DisplayName("Should create alimento successfully")
        void shouldCreateAlimentoSuccessfully() {
            // Given
            AlimentoDTO newAlimentoDTO = AlimentoDTO.builder()
                .nombre("Salmón a la plancha")
                .cal(208.0)
                .proteinas(25.4)
                .grasas(12.4)
                .hidratosdecarbono(0.0)
                .build();
                
            Alimento newAlimento = new Alimento();
            newAlimento.setNombre("Salmón a la plancha");
            newAlimento.setCal(208.0);
            newAlimento.setProteinas(25.4);
            newAlimento.setGrasas(12.4);
            newAlimento.setHidratosdecarbono(0.0);
            
            Alimento savedAlimento = new Alimento();
            savedAlimento.setId(2);
            savedAlimento.setNombre("Salmón a la plancha");
            savedAlimento.setCal(208.0);
            savedAlimento.setProteinas(25.4);
            savedAlimento.setGrasas(12.4);
            savedAlimento.setHidratosdecarbono(0.0);
            
            AlimentoDTO savedAlimentoDTO = AlimentoDTO.builder()
                .id(2)
                .nombre("Salmón a la plancha")
                .cal(208.0)
                .proteinas(25.4)
                .grasas(12.4)
                .hidratosdecarbono(0.0)
                .build();
            
            given(alimentoRepository.existsByNombreIgnoreCase("Salmón a la plancha")).willReturn(false);
            given(alimentoMapper.toEntity(newAlimentoDTO)).willReturn(newAlimento);
            given(alimentoRepository.save(newAlimento)).willReturn(savedAlimento);
            given(alimentoMapper.toDto(savedAlimento)).willReturn(savedAlimentoDTO);
            
            // When
            AlimentoDTO result = alimentoService.crear(newAlimentoDTO);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2);
            assertThat(result.getNombre()).isEqualTo("Salmón a la plancha");
            
            then(alimentoRepository).should().existsByNombreIgnoreCase("Salmón a la plancha");
            then(alimentoRepository).should().save(newAlimento);
        }
        
        @Test
        @DisplayName("Should throw BusinessException when creating alimento with duplicate name")
        void shouldThrowExceptionWhenCreatingDuplicateAlimento() {
            // Given
            AlimentoDTO duplicateAlimentoDTO = AlimentoDTO.builder()
                .nombre("Pollo a la plancha")
                .cal(165.0)
                .proteinas(25.0)
                .build();
                
            given(alimentoRepository.existsByNombreIgnoreCase("Pollo a la plancha")).willReturn(true);
            
            // When & Then
            assertThatThrownBy(() -> alimentoService.crear(duplicateAlimentoDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ya existe un alimento con el nombre: Pollo a la plancha");
            
            then(alimentoRepository).should().existsByNombreIgnoreCase("Pollo a la plancha");
            then(alimentoRepository).should(never()).save(any());
        }
        
        @Test
        @DisplayName("Should throw BusinessException when creating alimento without calories")
        void shouldThrowExceptionWhenCreatingAlimentoWithoutCalories() {
            // Given
            AlimentoDTO incompleteAlimentoDTO = AlimentoDTO.builder()
                .nombre("Alimento incompleto")
                .proteinas(10.0)
                // Missing calories
                .build();
            
            // When & Then
            assertThatThrownBy(() -> alimentoService.crear(incompleteAlimentoDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El alimento debe tener calorías y al menos un macronutriente (carbohidratos, grasas o proteínas)");
            
            then(alimentoRepository).should(never()).save(any());
        }
        
        @Test
        @DisplayName("Should update alimento successfully")
        void shouldUpdateAlimentoSuccessfully() {
            // Given
            AlimentoDTO updateDTO = AlimentoDTO.builder()
                .id(1)
                .nombre("Pollo a la plancha actualizado")
                .cal(170.0)
                .proteinas(26.0)
                .grasas(4.0)
                .hidratosdecarbono(0.0)
                .build();
                
            Alimento updatedAlimento = new Alimento();
            updatedAlimento.setId(1);
            updatedAlimento.setNombre("Pollo a la plancha actualizado");
            updatedAlimento.setCal(170.0);
            updatedAlimento.setProteinas(26.0);
            updatedAlimento.setGrasas(4.0);
            updatedAlimento.setHidratosdecarbono(0.0);
            
            given(alimentoRepository.findById(1)).willReturn(Optional.of(alimento));
            given(alimentoRepository.existsByNombreIgnoreCaseAndIdNot("Pollo a la plancha actualizado", 1))
                .willReturn(false);
            given(alimentoRepository.save(any(Alimento.class))).willReturn(updatedAlimento);
            given(alimentoMapper.toDto(updatedAlimento)).willReturn(updateDTO);
            willDoNothing().given(alimentoMapper).updateEntityFromDto(updateDTO, alimento);
            
            // When
            AlimentoDTO result = alimentoService.actualizar(1, updateDTO);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getNombre()).isEqualTo("Pollo a la plancha actualizado");
            assertThat(result.getCal()).isEqualTo(170.0);
            
            then(alimentoRepository).should().findById(1);
            then(alimentoMapper).should().updateEntityFromDto(updateDTO, alimento);
            then(alimentoRepository).should().save(alimento);
        }
        
        @Test
        @DisplayName("Should delete alimento successfully")
        void shouldDeleteAlimentoSuccessfully() {
            // Given
            given(alimentoRepository.existsById(1)).willReturn(true);
            willDoNothing().given(alimentoRepository).deleteById(1);
            
            // When
            alimentoService.eliminar(1);
            
            // Then
            then(alimentoRepository).should().existsById(1);
            then(alimentoRepository).should().deleteById(1);
        }
        
        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleting non-existent alimento")
        void shouldThrowExceptionWhenDeletingNonExistentAlimento() {
            // Given
            given(alimentoRepository.existsById(999)).willReturn(false);
            
            // When & Then
            assertThatThrownBy(() -> alimentoService.eliminar(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Alimento no encontrado con ID: 999");
            
            then(alimentoRepository).should().existsById(999);
            then(alimentoRepository).should(never()).deleteById(anyInt());
        }
    }
    
    @Nested
    @DisplayName("Business Logic Validation Tests")
    class BusinessLogicValidationTests {
        
        @Test
        @DisplayName("Should validate negative calories")
        void shouldValidateNegativeCalories() {
            // Given
            AlimentoDTO invalidAlimentoDTO = AlimentoDTO.builder()
                .nombre("Alimento inválido")
                .cal(-10.0) // Negative calories
                .proteinas(5.0)
                .build();
            
            // When & Then
            assertThatThrownBy(() -> alimentoService.crear(invalidAlimentoDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Las calorías no pueden ser negativas");
        }
        
        @Test
        @DisplayName("Should validate nutritional completeness for create")
        void shouldValidateNutritionalCompletenessForCreate() {
            // Given
            AlimentoDTO incompleteAlimentoDTO = AlimentoDTO.builder()
                .nombre("Alimento incompleto")
                // Missing calories and macronutrients
                .build();
            
            // When & Then
            assertThatThrownBy(() -> alimentoService.crear(incompleteAlimentoDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El alimento debe tener calorías y al menos un macronutriente (carbohidratos, grasas o proteínas)");
        }
        
        @Test
        @DisplayName("Should validate partial update with negative values")
        void shouldValidatePartialUpdateWithNegativeValues() {
            // Given
            AlimentoDTO partialUpdateDTO = AlimentoDTO.builder()
                .proteinas(-5.0) // Negative protein
                .build();
                
            given(alimentoRepository.findById(1)).willReturn(Optional.of(alimento));
            
            // When & Then
            assertThatThrownBy(() -> alimentoService.actualizarParcial(1, partialUpdateDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Las proteínas no pueden ser negativas");
                
            then(alimentoRepository).should().findById(1);
            then(alimentoRepository).should(never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("Advanced Search Tests")
    class AdvancedSearchTests {
        
        @Test
        @DisplayName("Should find foods by calorie range")
        void shouldFindFoodsByCalorieRange() {
            // Given
            Double minCal = 100.0;
            Double maxCal = 200.0;
            List<Alimento> calorieRangeAlimentos = Arrays.asList(alimento);
            Page<Alimento> calorieRangePage = new PageImpl<>(calorieRangeAlimentos, pageable, 1);
            
            given(alimentoRepository.findByCalorieRange(minCal, maxCal, pageable))
                .willReturn(calorieRangePage);
            given(alimentoMapper.toDto(alimento)).willReturn(alimentoDTO);
            
            // When
            Page<AlimentoDTO> result = alimentoService.buscarPorRangoCalorias(minCal, maxCal, pageable);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getCal()).isBetween(minCal, maxCal);
            
            then(alimentoRepository).should().findByCalorieRange(minCal, maxCal, pageable);
        }
        
        @Test
        @DisplayName("Should find diet suitable foods")
        void shouldFindDietSuitableFoods() {
            // Given
            Double minCal = 150.0;
            Double maxCal = 200.0;
            Double minProtein = 20.0;
            Double maxFat = 5.0;
            
            List<Alimento> dietSuitableAlimentos = Arrays.asList(alimento);
            Page<Alimento> dietSuitablePage = new PageImpl<>(dietSuitableAlimentos, pageable, 1);
            
            given(alimentoRepository.findDietSuitableFoods(minCal, maxCal, minProtein, maxFat, pageable))
                .willReturn(dietSuitablePage);
            given(alimentoMapper.toDto(alimento)).willReturn(alimentoDTO);
            
            // When
            Page<AlimentoDTO> result = alimentoService.buscarAlimentosParaDieta(
                minCal, maxCal, minProtein, maxFat, pageable);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            
            AlimentoDTO resultAlimento = result.getContent().get(0);
            assertThat(resultAlimento.getCal()).isBetween(minCal, maxCal);
            assertThat(resultAlimento.getProteinas()).isGreaterThanOrEqualTo(minProtein);
            assertThat(resultAlimento.getGrasas()).isLessThanOrEqualTo(maxFat);
            
            then(alimentoRepository).should().findDietSuitableFoods(minCal, maxCal, minProtein, maxFat, pageable);
        }
    }
}
