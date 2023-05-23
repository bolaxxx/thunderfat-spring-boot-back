package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MensajeDTO {
    private int idMensaje;
    private String contenido;
    private LocalDateTime timestamp;
    private UsuarioDTO emisor;
    // Otros atributos relevantes
}

