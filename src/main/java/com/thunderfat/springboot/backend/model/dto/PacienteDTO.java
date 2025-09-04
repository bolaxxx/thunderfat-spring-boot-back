package com.thunderfat.springboot.backend.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {
    private Integer id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellidos;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
    private LocalDate fechanacimiento;
    
    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    private String direccion;
    
    @Size(max = 100, message = "La localidad no puede superar los 100 caracteres")
    private String localidad;
    
    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal debe tener 5 dígitos")
    private String codigopostal;
    
    @Size(max = 50, message = "La provincia no puede superar los 50 caracteres")
    private String provincia;
    
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Za-z]$", message = "El DNI debe tener el formato correcto (8 dígitos + letra)")
    private String dni;
    
    @Positive(message = "La altura debe ser un valor positivo")
    private Double altura;
    
    @Pattern(regexp = "^[+]?[0-9]{9,15}$", message = "El teléfono debe tener un formato válido")
    private String telefono;
    
    @Pattern(regexp = "^(MASCULINO|FEMENINO|OTRO)$", message = "El sexo debe ser MASCULINO, FEMENINO u OTRO")
    private String sexo;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    // La contraseña solo se incluye en escritura, nunca en lectura
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String psw;
    
    private Boolean enabled;
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
