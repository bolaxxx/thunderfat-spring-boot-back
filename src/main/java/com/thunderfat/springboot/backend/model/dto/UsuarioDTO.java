package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class UsuarioDTO {
    private int id;
    private String psw;
    private LocalDate createtime;
    private String email;
    private boolean enabled; 
    private List<RolDTO> roles;
    // Otros atributos relevantes
}