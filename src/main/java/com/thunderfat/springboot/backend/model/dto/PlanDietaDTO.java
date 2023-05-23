package com.thunderfat.springboot.backend.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class PlanDietaDTO {
    private int id;
    private String nombre;
    private List<DiaDietaDTO> diasDieta;
    private List<FiltroAlimentarioDTO> filtrosAplicados;
    // Otros atributos relevantes
}
