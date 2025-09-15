package com.thunderfat.springboot.backend.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.PlatoPredeterminadoRepository;
import com.thunderfat.springboot.backend.model.dto.PlatoPredeterminadoDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.PlatoPredeterminadoMapper;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

/**
 * Unit tests for the modernized PlatoPredeterminadoJPA service.
 * Demonstrates Spring Boot 2025 testing best practices:
 * - Using MockitoExtension for clean mocking
 * - Clear test naming and organization
 * - Proper AAA pattern (Arrange-Act-Assert)
 * - Edge case testing
 * - Exception testing
 * 
 * @author ThunderFat Development Team
 */
@ExtendWith(MockitoExtension.class)
class PlatoPredeterminadoJPATest {

    @Mock
    private PlatoPredeterminadoRepository repository;
    
    @Mock
    private INutricionistaService nutricionistaService;
    
    @Mock
    private PlatoPredeterminadoMapper mapper;
    
    @InjectMocks
    private PlatoPredeterminadoJPA service;
    
    private PlatoPredeterminado platoPredeterminado;
    private PlatoPredeterminadoDTO platoPredeterminadoDTO;
    private Nutricionista nutricionista;
    private List<PlatoPredeterminado> platoPredeterminadoList;
    private List<PlatoPredeterminadoDTO> platoPredeterminadoDTOList;

    @BeforeEach
    void setUp() {
        // Set up test data
        platoPredeterminado = new PlatoPredeterminado();
        platoPredeterminado.setId(1);
        platoPredeterminado.setNombre("Test Dish");
        
        platoPredeterminadoDTO = new PlatoPredeterminadoDTO();
        platoPredeterminadoDTO.setId(1);
        platoPredeterminadoDTO.setNombre("Test Dish");
        platoPredeterminadoDTO.setNutricionistaId(1);
        
        nutricionista = new Nutricionista();
        nutricionista.setId(1);
        
        platoPredeterminadoList = Arrays.asList(platoPredeterminado);
        platoPredeterminadoDTOList = Arrays.asList(platoPredeterminadoDTO);
    }

    @Test
    @DisplayName("Should find dish by ID")
    void shouldFindDishById() {
        // Arrange
        lenient().when(repository.findById(1)).thenReturn(Optional.of(platoPredeterminado));
        lenient().when(mapper.toDto(platoPredeterminado)).thenReturn(platoPredeterminadoDTO);
        
        // Act
        Optional<PlatoPredeterminadoDTO> result = service.findById(1);
        
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return empty when dish not found")
    void shouldReturnEmptyWhenDishNotFound() {
        // Arrange
        when(repository.findById(99)).thenReturn(Optional.empty());
        
        // Act
        Optional<PlatoPredeterminadoDTO> result = service.findById(99);
        
        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find all dishes by nutritionist ID")
    void shouldFindAllDishesByNutricionistId() {
        // Arrange
        lenient().when(repository.findByNutricionistaId(1)).thenReturn(platoPredeterminadoList);
        lenient().when(mapper.toDtoList(platoPredeterminadoList)).thenReturn(platoPredeterminadoDTOList);
        
        // Act
        List<PlatoPredeterminadoDTO> result = service.findAllByNutricionistaId(1);
        
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should create new dish")
    void shouldCreateNewDish() {
        // Arrange
        lenient().when(mapper.toEntity(platoPredeterminadoDTO)).thenReturn(platoPredeterminado);
        lenient().when(nutricionistaService.buscarPorId(1)).thenReturn(nutricionista);
        lenient().when(repository.save(any(PlatoPredeterminado.class))).thenReturn(platoPredeterminado);
        lenient().when(mapper.toDto(platoPredeterminado)).thenReturn(platoPredeterminadoDTO);
        
        // Act
        PlatoPredeterminadoDTO result = service.create(platoPredeterminadoDTO, 1);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw exception when nutritionist not found")
    void shouldThrowExceptionWhenNutricionistNotFound() {
        // Arrange
        when(nutricionistaService.buscarPorId(99)).thenReturn(null);
        
        // Act & Assert
        assertThatThrownBy(() -> service.create(platoPredeterminadoDTO, 99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Nutritionist not found");
    }

    @Test
    @DisplayName("Should delete dish by ID")
    void shouldDeleteDishById() {
        // Arrange
        when(repository.existsById(1)).thenReturn(true);
        
        // Act
        service.deleteById(1);
        
        // Assert
        verify(repository).deleteById(1);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent dish")
    void shouldThrowExceptionWhenDeletingNonExistentDish() {
        // Arrange
        when(repository.existsById(99)).thenReturn(false);
        
        // Act & Assert
        assertThatThrownBy(() -> service.deleteById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("not found");
    }
    
    @Test
    @DisplayName("Legacy method buscarPorId should delegate to modern findById")
    void legacyBuscarPorIdShouldDelegateToModernFindById() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.of(platoPredeterminado));
        
        // Act
        PlatoPredeterminado result = service.buscarPorId(1);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }
}
