package com.thunderfat.springboot.backend.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class FiltroAlimentarioDTO {
    private int id;
    private String nombre;
    private String descripcion;
    private List<AlimentoDTO> alimentos;
    // Otros atributos relevantes
}
