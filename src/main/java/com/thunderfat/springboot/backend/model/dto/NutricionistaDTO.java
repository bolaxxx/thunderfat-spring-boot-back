package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
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
public class NutricionistaDTO {
    private Integer id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellidos;
    
    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "El teléfono debe tener un formato válido")
    private String telefono;
    
    @Size(max = 100, message = "La localidad no puede superar los 100 caracteres")
    private String localidad;
    
    @Size(max = 50, message = "La provincia no puede superar los 50 caracteres")
    private String provincia;
    
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Za-z]$", message = "El DNI debe tener el formato correcto (8 dígitos + letra)")
    private String dni;
    
    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;

   @NotBlank(message = "El número de colegiado profesional es obligatorio")
   @Size(min = 4, max = 30, message = "El número de colegiado profesional debe tener entre 4 y 30 caracteres")
   private String numeroColegiadoProfesional;
    
    // Fields from Usuario base class
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    // La contraseña solo se incluye en escritura, nunca en lectura
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String psw;
    
    private Boolean enabled;
    private LocalDateTime createtime;
    
    // Relationships as ID lists
    private List<Integer> pacientesIds;
    private List<Integer> citasIds;
}
