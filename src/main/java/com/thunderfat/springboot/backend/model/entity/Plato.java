package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plato")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
// @DiscriminatorColumn(name="type",discriminatorType=DiscriminatorType.STRING)
public class Plato implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String nombre;
	
	@OneToMany( fetch = FetchType.LAZY)
	@JoinColumn(name="id_plato")
	private List<Ingrediente> ingredientes;
	
	@Lob
	@Column(name = "receta", length = 512)
	private String receta;
	
	private double proteinastotales;
	private double grasastotales;
	private double kcaltotales;
	private double hidratostotales;
	private double fibra;
	private double sal;
	private double azucar;
	private double colesterol;
	private double sodio;
	private double potasio;
	private double calcio;
	private double hierro;
	private double magnesio;
	private double fosforo;
	private double zinc;
	private double vitaminaA;
	private double vitaminaC;
	private double vitaminaD;
	private double vitaminaB6;
	private double vitaminaB12;
	private double vitaminaE;
	private double vitaminaK;
	private double tiamina;
	private double riboflavina;
	private double niacina;
	private double acfolico;
	private double pantotenico;
	private double biotina;
	private double vitaminaB12_2;
	private double vitaminaC_2;
	private double sodio_2;
	private double potasio_2;
	private double calcio_2;
	private double hierro_2;
	private double magnesio_2;
	private double fosforo_2;
	private double zinc_2;
	private double vitaminaA_2;

	public Plato(String nombre, String receta) {
		super();
		this.nombre = nombre;
		this.receta = receta;
		this.ingredientes = new ArrayList<>();
	}
}