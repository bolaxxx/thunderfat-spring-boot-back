package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CitaDTO {
    private int id;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private PacienteDTO paciente;
    private NutricionistaDTO nutricionista;
    // Otros atributos relevantes
}
