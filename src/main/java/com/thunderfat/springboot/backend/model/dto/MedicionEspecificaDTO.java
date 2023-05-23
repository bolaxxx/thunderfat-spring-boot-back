package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MedicionEspecificaDTO {
    private int id;
    private double grasavisceral;
    private double retencionliquidos;
    private double aguaid;
    private double litrosagua;
    private double porcentajeagua;
    private double musculoidmax;
    private double musculoidmin;
    private double mbi;
    private double metabolismo;
    private int edadmet;
    private double musculo;
    private double masaosea;
    private double poxmusmax;
    private double poxmusmin;
    private double porcentajegrasa;
    private double grasas;
    private double grasasidmin;
    private double grasaidmax;
    private double peso;
    private LocalDate fecha;
    // Otros atributos relevantes
}

