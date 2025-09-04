package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private Integer idChat;
    private Integer pacienteId;
    private Integer nutricionistaId;
    private LocalDateTime fechahora;
    private List<Integer> mensajesIds;
}

