package com.thunderfat.springboot.backend.model.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.thunderfat.springboot.backend.model.dao.CitaRepository;
import com.thunderfat.springboot.backend.model.dto.CitaDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.CitaMapper;
import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

/**
 * Comprehensive test suite for CitaServiceJPAModern.
 * Follows Spring Boot 2025 testing best practices including:
 * - Mockito-based unit testing
 * - Nested test classes for organization
 * - AssertJ for fluent assertions
 * - DisplayName annotations for clarity
 * - Comprehensive business logic testing
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CitaService Modern Implementation Tests")
class CitaServiceJPAModernTest {
    
    @Mock
    private CitaRepository citaRepository;
    
    @Mock
    private CitaMapper citaMapper;
    
    @InjectMocks
    private CitaServiceJPA citaService;
    
    // Test data
    private CitaDTO testCitaDTO;
    private Cita testCita;
    private Paciente testPaciente;
    private Nutricionista testNutricionista;
    
    @BeforeEach
    void setUp() {
        // Setup test entities
        testPaciente = new Paciente();
        testPaciente.setId(1);
        testPaciente.setNombre("Juan");
        testPaciente.setApellidos("Pérez");
        
        testNutricionista = new Nutricionista();
        testNutricionista.setId(1);
        testNutricionista.setNombre("Ana");
        testNutricionista.setApellidos("García");
        
        testCita = new Cita();
        testCita.setId(1);
        testCita.setFechaini(LocalDateTime.now().plusDays(1));
        testCita.setFechafin(LocalDateTime.now().plusDays(1).plusHours(1));
        testCita.setPaciente(testPaciente);
        testCita.setNutricionista(testNutricionista);
        
        testCitaDTO = CitaDTO.builder()
                .id(1)
                .fechaInicio(LocalDateTime.now().plusDays(1))
                .fechaFin(LocalDateTime.now().plusDays(1).plusHours(1))
                .pacienteId(1)
                .nutricionistaId(1)
                .build();
    }
    
    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperations {
        
        @Test
        @DisplayName("Should find appointment by ID successfully")
        void shouldFindAppointmentById() {
            // Given
            when(citaRepository.findById(1)).thenReturn(Optional.of(testCita));
            when(citaMapper.toDto(testCita)).thenReturn(testCitaDTO);
            
            // When
            Optional<CitaDTO> result = citaService.findById(1);
            
            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1);
            verify(citaRepository).findById(1);
            verify(citaMapper).toDto(testCita);
        }
        
        @Test
        @DisplayName("Should return empty when appointment not found")
        void shouldReturnEmptyWhenAppointmentNotFound() {
            // Given
            when(citaRepository.findById(999)).thenReturn(Optional.empty());
            
            // When
            Optional<CitaDTO> result = citaService.findById(999);
            
            // Then
            assertThat(result).isEmpty();
            verify(citaRepository).findById(999);
            verifyNoInteractions(citaMapper);
        }
        
        @Test
        @DisplayName("Should create appointment successfully")
        void shouldCreateAppointmentSuccessfully() {
            // Given
            testCitaDTO.setId(null); // New appointment
            Cita newCita = new Cita();
            newCita.setId(1);
            
            when(citaMapper.toEntity(testCitaDTO)).thenReturn(testCita);
            when(citaRepository.findConflictingAppointments(any(), any(), any())).thenReturn(Arrays.asList());
            when(citaRepository.save(testCita)).thenReturn(newCita);
            when(citaMapper.toDto(newCita)).thenReturn(testCitaDTO);
            
            // When
            CitaDTO result = citaService.create(testCitaDTO);
            
            // Then
            assertThat(result).isNotNull();
            verify(citaRepository).findConflictingAppointments(any(), any(), any());
            verify(citaRepository).save(testCita);
        }
        
        @Test
        @DisplayName("Should update appointment successfully")
        void shouldUpdateAppointmentSuccessfully() {
            // Given
            when(citaRepository.existsById(1)).thenReturn(true);
            when(citaMapper.toEntity(testCitaDTO)).thenReturn(testCita);
            when(citaRepository.findConflictingAppointmentsExcluding(any(), any(), any(), any())).thenReturn(Arrays.asList());
            when(citaRepository.save(testCita)).thenReturn(testCita);
            when(citaMapper.toDto(testCita)).thenReturn(testCitaDTO);
            
            // When
            CitaDTO result = citaService.update(1, testCitaDTO);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            verify(citaRepository).existsById(1);
            verify(citaRepository).findConflictingAppointmentsExcluding(any(), any(), any(), eq(1));
            verify(citaRepository).save(testCita);
        }
        
        @Test
        @DisplayName("Should throw exception when updating non-existent appointment")
        void shouldThrowExceptionWhenUpdatingNonExistentAppointment() {
            // Given
            when(citaRepository.existsById(999)).thenReturn(false);
            
            // When & Then
            assertThatThrownBy(() -> citaService.update(999, testCitaDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Cita no encontrada con ID: 999");
            
            verify(citaRepository).existsById(999);
            verifyNoMoreInteractions(citaRepository);
        }
        
        @Test
        @DisplayName("Should delete appointment successfully")
        void shouldDeleteAppointmentSuccessfully() {
            // Given
            when(citaRepository.existsById(1)).thenReturn(true);
            
            // When
            citaService.deleteById(1);
            
            // Then
            verify(citaRepository).existsById(1);
            verify(citaRepository).deleteById(1);
        }
        
        @Test
        @DisplayName("Should throw exception when deleting non-existent appointment")
        void shouldThrowExceptionWhenDeletingNonExistentAppointment() {
            // Given
            when(citaRepository.existsById(999)).thenReturn(false);
            
            // When & Then
            assertThatThrownBy(() -> citaService.deleteById(999))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Cita no encontrada con ID: 999");
            
            verify(citaRepository).existsById(999);
            verify(citaRepository, never()).deleteById(any());
        }
    }
    
    @Nested
    @DisplayName("Pagination Operations")
    class PaginationOperations {
        
        @Test
        @DisplayName("Should find appointments by patient ID with pagination")
        void shouldFindAppointmentsByPatientIdWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cita> citasPage = new PageImpl<>(Arrays.asList(testCita), pageable, 1);
            
            when(citaRepository.findByPacienteId(1, pageable)).thenReturn(citasPage);
            when(citaMapper.toDto(testCita)).thenReturn(testCitaDTO);
            
            // When
            Page<CitaDTO> result = citaService.findByPacienteId(1, pageable);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(citaRepository).findByPacienteId(1, pageable);
        }
        
        @Test
        @DisplayName("Should find appointments by nutritionist ID with pagination")
        void shouldFindAppointmentsByNutricionistaIdWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cita> citasPage = new PageImpl<>(Arrays.asList(testCita), pageable, 1);
            
            when(citaRepository.findByNutricionistaId(1, pageable)).thenReturn(citasPage);
            when(citaMapper.toDto(testCita)).thenReturn(testCitaDTO);
            
            // When
            Page<CitaDTO> result = citaService.findByNutricionistaId(1, pageable);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(citaRepository).findByNutricionistaId(1, pageable);
        }
        
        @Test
        @DisplayName("Should find appointments by date range with pagination")
        void shouldFindAppointmentsByDateRangeWithPagination() {
            // Given
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().plusDays(7);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cita> citasPage = new PageImpl<>(Arrays.asList(testCita), pageable, 1);
            
            when(citaRepository.findByDateRange(startDate, endDate, pageable)).thenReturn(citasPage);
            when(citaMapper.toDto(testCita)).thenReturn(testCitaDTO);
            
            // When
            Page<CitaDTO> result = citaService.findByDateRange(startDate, endDate, pageable);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            verify(citaRepository).findByDateRange(startDate, endDate, pageable);
        }
    }
    
    @Nested
    @DisplayName("Business Logic Validations")
    class BusinessLogicValidations {
        
        @Test
        @DisplayName("Should validate appointment scheduling successfully")
        void shouldValidateAppointmentSchedulingSuccessfully() {
            // Given - test with existing appointment (has ID, so will use validateNoConflictsForUpdate)
            when(citaRepository.findConflictingAppointmentsExcluding(any(), any(), any(), any())).thenReturn(Arrays.asList());
            
            // When
            boolean result = citaService.validateAppointmentScheduling(testCitaDTO);
            
            // Then
            assertThat(result).isTrue();
        }
        
        @Test
        @DisplayName("Should fail validation for conflicting appointments")
        void shouldFailValidationForConflictingAppointments() {
            // Given - test with existing appointment (has ID, so will use validateNoConflictsForUpdate)
            when(citaRepository.findConflictingAppointmentsExcluding(any(), any(), any(), any())).thenReturn(Arrays.asList(testCita));
            
            // When
            boolean result = citaService.validateAppointmentScheduling(testCitaDTO);
            
            // Then
            assertThat(result).isFalse();
        }
        
        @Test
        @DisplayName("Should validate new appointment scheduling successfully")
        void shouldValidateNewAppointmentSchedulingSuccessfully() {
            // Given - test with new appointment (no ID, so will use validateNoConflicts)
            CitaDTO newCitaDTO = CitaDTO.builder()
                    .id(null)  // New appointment
                    .fechaInicio(LocalDateTime.now().plusDays(2))
                    .fechaFin(LocalDateTime.now().plusDays(2).plusHours(1))
                    .pacienteId(1)
                    .nutricionistaId(1)
                    .build();
                    
            when(citaRepository.findConflictingAppointments(any(), any(), any())).thenReturn(Arrays.asList());
            
            // When
            boolean result = citaService.validateAppointmentScheduling(newCitaDTO);
            
            // Then
            assertThat(result).isTrue();
        }
        
        @Test
        @DisplayName("Should fail validation for conflicting new appointments")
        void shouldFailValidationForConflictingNewAppointments() {
            // Given - test with new appointment (no ID, so will use validateNoConflicts)
            CitaDTO newCitaDTO = CitaDTO.builder()
                    .id(null)  // New appointment
                    .fechaInicio(LocalDateTime.now().plusDays(2))
                    .fechaFin(LocalDateTime.now().plusDays(2).plusHours(1))
                    .pacienteId(1)
                    .nutricionistaId(1)
                    .build();
                    
            when(citaRepository.findConflictingAppointments(any(), any(), any())).thenReturn(Arrays.asList(testCita));
            
            // When
            boolean result = citaService.validateAppointmentScheduling(newCitaDTO);
            
            // Then
            assertThat(result).isFalse();
        }
        
        @Test
        @DisplayName("Should throw exception for invalid time range")
        void shouldThrowExceptionForInvalidTimeRange() {
            // Given
            testCitaDTO.setFechaFin(testCitaDTO.getFechaInicio().minusHours(1)); // End before start
            
            // When & Then
            assertThatThrownBy(() -> citaService.create(testCitaDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        @Test
        @DisplayName("Should throw exception for appointment too short")
        void shouldThrowExceptionForAppointmentTooShort() {
            // Given
            testCitaDTO.setFechaFin(testCitaDTO.getFechaInicio().plusMinutes(10)); // Only 10 minutes
            
            // When & Then
            assertThatThrownBy(() -> citaService.create(testCitaDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("La duración mínima de una cita es de 15 minutos");
        }
        
        @Test
        @DisplayName("Should throw exception for appointment too long")
        void shouldThrowExceptionForAppointmentTooLong() {
            // Given
            testCitaDTO.setFechaFin(testCitaDTO.getFechaInicio().plusHours(5)); // 5 hours
            
            // When & Then
            assertThatThrownBy(() -> citaService.create(testCitaDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("La duración máxima de una cita es de 4 horas");
        }
        
        @Test
        @DisplayName("Should throw exception for appointment in the past")
        void shouldThrowExceptionForAppointmentInThePast() {
            // Given
            testCitaDTO.setId(null); // New appointment
            testCitaDTO.setFechaInicio(LocalDateTime.now().minusHours(1)); // In the past
            testCitaDTO.setFechaFin(LocalDateTime.now().plusHours(1));
            
            // When & Then
            assertThatThrownBy(() -> citaService.create(testCitaDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("No se pueden crear citas en el pasado");
        }
    }
    
    @Nested
    @DisplayName("Analytics and Statistics")
    class AnalyticsAndStatistics {
        
        @Test
        @DisplayName("Should count appointments by patient ID")
        void shouldCountAppointmentsByPacienteId() {
            // Given
            when(citaRepository.countByPacienteId(1)).thenReturn(5L);
            
            // When
            Long result = citaService.countAppointmentsByPacienteId(1);
            
            // Then
            assertThat(result).isEqualTo(5L);
            verify(citaRepository).countByPacienteId(1);
        }
        
        @Test
        @DisplayName("Should count appointments by nutritionist ID")
        void shouldCountAppointmentsByNutricionistaId() {
            // Given
            when(citaRepository.countByNutricionistaId(1)).thenReturn(10L);
            
            // When
            Long result = citaService.countAppointmentsByNutricionistaId(1);
            
            // Then
            assertThat(result).isEqualTo(10L);
            verify(citaRepository).countByNutricionistaId(1);
        }
        
        @Test
        @DisplayName("Should get appointment statistics")
        void shouldGetAppointmentStatistics() {
            // Given
            Object[] statsArray = {3L, 1L, 6L}; // upcoming, ongoing, completed
            when(citaRepository.getAppointmentStatistics(eq(1), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(statsArray);
            
            // When
            var result = citaService.getAppointmentStatistics(1, LocalDate.now(), LocalDate.now().plusDays(7));
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("upcoming")).isEqualTo(3L);
            assertThat(result.get("ongoing")).isEqualTo(1L);
            assertThat(result.get("completed")).isEqualTo(6L);
        }
        
        @Test
        @DisplayName("Should get calendar events")
        void shouldGetCalendarEvents() {
            // Given
            Object[] eventArray = {1, "Juan Pérez", testCitaDTO.getFechaInicio(), testCitaDTO.getFechaFin(), 1};
            List<Object[]> eventsList = Arrays.<Object[]>asList(eventArray);
            when(citaRepository.getCalendarEvents(eq(1), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(eventsList);
            
            // When
            var result = citaService.getCalendarEvents(1, LocalDate.now(), LocalDate.now().plusDays(7));
            
            // Then
            assertThat(result).hasSize(1);
            var event = result.get(0);
            assertThat(event.get("id")).isEqualTo(1);
            assertThat(event.get("title")).isEqualTo("Juan Pérez");
            assertThat(event.get("pacienteId")).isEqualTo(1);
        }
    }
    
    @Nested
    @DisplayName("Legacy Method Compatibility")
    class LegacyMethodCompatibility {
        
        @Test
        @DisplayName("Should support legacy listar method")
        @SuppressWarnings("deprecation")
        void shouldSupportLegacyListarMethod() {
            // Given
            when(citaRepository.findAll()).thenReturn(Arrays.asList(testCita));
            when(citaMapper.toDto(testCita)).thenReturn(testCitaDTO);
            
            // When
            List<CitaDTO> result = citaService.listar();
            
            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1);
            verify(citaRepository).findAll();
        }
        
        @Test
        @DisplayName("Should support legacy buscarPorId method")
        @SuppressWarnings("deprecation")
        void shouldSupportLegacyBuscarPorIdMethod() {
            // Given
            when(citaRepository.findById(1)).thenReturn(Optional.of(testCita));
            when(citaMapper.toDto(testCita)).thenReturn(testCitaDTO);
            
            // When
            CitaDTO result = citaService.buscarPorId(1);
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            verify(citaRepository).findById(1);
        }
        
        @Test
        @DisplayName("Should support legacy buscarPorPaciente method")
        @SuppressWarnings("deprecation")
        void shouldSupportLegacyBuscarPorPacienteMethod() {
            // Given
            when(citaRepository.buscarPorPaciente(1)).thenReturn(Arrays.asList(testCita));
            when(citaMapper.toDto(testCita)).thenReturn(testCitaDTO);
            
            // When
            List<CitaDTO> result = citaService.buscarPorPaciente(1);
            
            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1);
            verify(citaRepository).buscarPorPaciente(1);
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {
        
        @Test
        @DisplayName("Should handle null date range validation")
        void shouldHandleNullDateRangeValidation() {
            // When & Then
            assertThatThrownBy(() -> citaService.findByDateRange(null, LocalDate.now(), PageRequest.of(0, 10)))
                    .isInstanceOf(RuntimeException.class);
        }
        
        @Test
        @DisplayName("Should handle invalid date range")
        void shouldHandleInvalidDateRange() {
            // Given
            LocalDate startDate = LocalDate.now().plusDays(7);
            LocalDate endDate = LocalDate.now();
            
            // When & Then
            assertThatThrownBy(() -> citaService.findByDateRange(startDate, endDate, PageRequest.of(0, 10)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("La fecha de inicio debe ser anterior a la fecha de fin");
        }
        
        @Test
        @DisplayName("Should handle empty statistics result")
        void shouldHandleEmptyStatisticsResult() {
            // Given
            when(citaRepository.getAppointmentStatistics(eq(1), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(null);
            
            // When
            var result = citaService.getAppointmentStatistics(1, LocalDate.now(), LocalDate.now().plusDays(7));
            
            // Then
            assertThat(result).isNotNull();
            assertThat(result.get("upcoming")).isEqualTo(0L);
            assertThat(result.get("ongoing")).isEqualTo(0L);
            assertThat(result.get("completed")).isEqualTo(0L);
        }
    }
}
