package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatDTO {
    private int id;
    private LocalDateTime fecha;
    private String contenido;
    private UsuarioDTO usuarioEmisor;
    private UsuarioDTO usuarioReceptor;
    // Otros atributos relevantes
}

