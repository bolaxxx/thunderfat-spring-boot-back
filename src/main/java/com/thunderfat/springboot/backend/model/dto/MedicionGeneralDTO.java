package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class MedicionGeneralDTO {
    private int id;
    private LocalDate fecha;
    private double pesoideal;
    private double pesoactual;
    private double brazo;
    private double icc;
    private double porcentajegrasas;
    private double imc;
    private double tensionmin;
    private double tensionmax;
    private double cadera;
    private double cintura;
    private double muslo;
    private double pantorrilla;
    private double pecho;
    private double abdomen;
    private double cuello;
    private PacienteDTO paciente;
    // Otros atributos relevantes
}
