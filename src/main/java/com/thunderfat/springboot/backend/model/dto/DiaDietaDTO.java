package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class DiaDietaDTO {
    private int id;
    private LocalDate fecha;
    private List<ComidaDTO> comidas;
    // Otros atributos relevantes
}
