package com.thunderfat.springboot.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {
    private Integer id;
    
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre del rol debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^ROLE_[A-Z_]+$", message = "El nombre del rol debe seguir el patrón ROLE_XXXXX")
    private String nombre;
    
    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    private String descripcion;
    
    @lombok.Builder.Default
    private Boolean activo = true;
}
