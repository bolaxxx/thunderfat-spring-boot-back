package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AntecedentesClinicosDTO {
    private int id;
    private LocalDate fecha;
    private String antecedente;
    private String observacion;
    private String descripcion;   
    private String condicion;
    private int idPaciente;

}
