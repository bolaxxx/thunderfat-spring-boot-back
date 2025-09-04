package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Future;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDietaDTO {
    
    private Integer id;
    
    @NotNull(message = "El ID del paciente es obligatorio")
    @Positive(message = "El ID del paciente debe ser positivo")
    @JsonProperty("pacienteId")
    private Integer idPaciente;
    
    @NotNull(message = "El ID del nutricionista es obligatorio")
    @Positive(message = "El ID del nutricionista debe ser positivo")
    @JsonProperty("nutricionistaId")
    private Integer idNutricionista;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaini;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser en el futuro")
    private LocalDate fechafin;
    
    @PositiveOrZero(message = "El rango mínimo de calorías debe ser positivo o cero")
    private Double calrangomin;
    
    @Positive(message = "El rango máximo de calorías debe ser positivo")
    private Double calrangomax;
    
    @PositiveOrZero(message = "La ingesta calórica diaria debe ser positiva o cero")
    private Double ingestacaldiaria;
    
    @PositiveOrZero(message = "El reparto de glúcidos debe ser positivo o cero")
    private Double repartoglucidodiario;
    
    @PositiveOrZero(message = "El reparto de lípidos debe ser positivo o cero")
    private Double repartolipidodiario;
    
    @PositiveOrZero(message = "El reparto de prótidos debe ser positivo o cero")
    private Double repartoprotidodiario;
    
    @Min(value = 1, message = "Debe haber al menos 1 comida al día")
    @Max(value = 10, message = "No pueden haber más de 10 comidas al día")
    private Integer comidasdiarias;
    
    private Boolean visible;
    
    private Boolean intercambiable;
    
    // Relationships as nested DTOs or IDs
    private List<DiaDietaDTO> diasDieta;
    
    private Integer filtroAplicadoId;
    
    private FiltroAlimentarioDTO filtroAplicado;
    
    // Calculated fields for display purposes
    @JsonProperty(access = Access.READ_ONLY)
    private Boolean isActive;
    
    @JsonProperty(access = Access.READ_ONLY)
    private Integer daysRemaining;
    
    @JsonProperty(access = Access.READ_ONLY)
    private Integer totalDays;
    
    @JsonProperty(access = Access.READ_ONLY)
    private Double completionPercentage;
}
