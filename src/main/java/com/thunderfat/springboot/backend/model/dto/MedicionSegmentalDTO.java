package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MedicionSegmentalDTO {
    private int id;
    private LocalDate fecha;
    private double bdporcentajegrasas;
    private double bdmusculo;
    private double bimusculo;
    private double piporcentajegrasas;
    private double pdmusculo;
    private double pdporcentajegrasas;
    private double tporcentajegrasa;
    private double tmusculo;
    private double pimusculo;
    private double biporcentajegrasas;
    // Otros atributos relevantes
}
