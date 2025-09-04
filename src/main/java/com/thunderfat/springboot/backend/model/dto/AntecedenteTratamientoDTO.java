package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AntecedenteTratamientoDTO {
    private int id;
    private String antecedente;
    private LocalDate fecha;
    private String observacion;
    private int idPaciente;
}
