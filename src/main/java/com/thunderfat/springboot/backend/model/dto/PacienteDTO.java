package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class PacienteDTO {
    private Integer id;
    private String nombre;
    private String apellidos;
    private LocalDate fechanacimiento;
    private String direccion;
    private String localidad;
    private String codigopostal;
    private String provincia;
    private String dni;
    private double altura;
    private String telefono;
    private String sexo;
    private String email;
    private String psw;
    private boolean enabled;
    private LocalDateTime createtime;
    private Integer nutricionistaId;
    private List<Integer> citasIds;
    private List<Integer> medicionesEspecificasIds;
    private List<Integer> medicionesGeneralesIds;
    private List<Integer> medicionesSegmentalesIds;
    private List<Integer> antecedentesClinicosIds;
    private List<Integer> antecedentesTratamientosIds;
    private List<Integer> planesDietaIds;
}
