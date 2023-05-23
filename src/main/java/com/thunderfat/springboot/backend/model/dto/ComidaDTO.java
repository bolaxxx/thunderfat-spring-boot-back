package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalTime;
import java.util.List;

import lombok.Data;

@Data
public class ComidaDTO {
    private int id;
    private LocalTime hora;
    private int valoracion;
    private List<PlatoPlanDietaDTO> platos;
    // Otros atributos relevantes
}
