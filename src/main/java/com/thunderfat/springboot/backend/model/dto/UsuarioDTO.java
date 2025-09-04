package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Integer id;
    
    // La contraseña solo se incluye en escritura, nunca en lectura
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String psw;
    
    private LocalDateTime createtime;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    private Boolean enabled;
    
    private List<RolDTO> roles;
}