package com.thunderfat.springboot.backend.model.dto;

import lombok.Data;

@Data
public class IngredienteDTO {
    private int id;
    private AlimentoDTO alimento;
    private double cantidad;
    private double proteinastotales;
    private double grasastotales;
    private double kcaltotales;
    private double hidratostotales;
}
