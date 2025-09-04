package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alimento (Food) entity with Spring Boot 2025 improvements
 * Updated to use wrapper types for better null handling
 */
@Entity
@Table(name = "alimento")
@Data
@NoArgsConstructor
public class Alimento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;	
	
	@NotBlank(message = "El estado es obligatorio")
	private String estado;
	
	@NotNull(message = "Las calorías son obligatorias")
	@PositiveOrZero(message = "Las calorías no pueden ser negativas")
	private Double cal;
	
	@PositiveOrZero(message = "Los hidratos de carbono no pueden ser negativos")
	private Double hidratosdecarbono;
	
	@PositiveOrZero(message = "El contenido de agua no puede ser negativo")
	private Double h2o;	
	
	@PositiveOrZero(message = "El valor no específico no puede ser negativo")
	private Double noespecifico;
	
	@PositiveOrZero(message = "Las grasas no pueden ser negativas")
	private Double grasas;
	
	@PositiveOrZero(message = "Las proteínas no pueden ser negativas")
	private Double proteinas;	
	
	@PositiveOrZero(message = "La vitamina A no puede ser negativa")
	private Double vitamina;
	
	@PositiveOrZero(message = "La vitamina B2 no puede ser negativa")
	private Double vitaminb2;	
	
	@PositiveOrZero(message = "La vitamina B1 no puede ser negativa")
	private Double vitaminb1;
	
	@PositiveOrZero(message = "La vitamina C no puede ser negativa")
	private Double vitaminc;	
	
	@PositiveOrZero(message = "La niacina no puede ser negativa")
	private Double niac;	
	
	@PositiveOrZero(message = "El cobre no puede ser negativo")
	private Double cobre;	
	
	@PositiveOrZero(message = "El potasio no puede ser negativo")
	private Double potasio;	
	
	@PositiveOrZero(message = "El sodio no puede ser negativo")
	private Double sodio;	
	
	@PositiveOrZero(message = "El azufre no puede ser negativo")
	private Double azufre;	
	
	@PositiveOrZero(message = "El calcio no puede ser negativo")
	private Double calcio;	
	
	@PositiveOrZero(message = "El fósforo no puede ser negativo")
	private Double fosforo;
	
	@PositiveOrZero(message = "El hierro no puede ser negativo")
	private Double hierro;	
	
	@PositiveOrZero(message = "El magnesio no puede ser negativo")
	private Double magnesio;	
	
	@PositiveOrZero(message = "El cloro no puede ser negativo")
	private Double cloro;	
	
	@PositiveOrZero(message = "La metionina no puede ser negativa")
	private Double met; 	
	
	@PositiveOrZero(message = "La lisina no puede ser negativa")
	private Double lis;
	
	@PositiveOrZero(message = "La leucina no puede ser negativa")
	private Double leu;
	
	@PositiveOrZero(message = "La isoleucina no puede ser negativa")
	private Double illeu;	
	
	@PositiveOrZero(message = "La treonina no puede ser negativa")
	private Double tre;
	
	@PositiveOrZero(message = "El triptófano no puede ser negativo")
	private Double tri;	
	
	@PositiveOrZero(message = "La fenilalanina no puede ser negativa")
	private Double fen;
	
	@PositiveOrZero(message = "La valina no puede ser negativa")
	private Double val;
	
	@PositiveOrZero(message = "Los ácidos no pueden ser negativos")
	private Double acid;
	
	@PositiveOrZero(message = "Los alcalinos no pueden ser negativos")
	private Double alcal;
}
