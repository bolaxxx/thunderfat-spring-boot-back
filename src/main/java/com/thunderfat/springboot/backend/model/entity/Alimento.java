package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;


import org.springframework.data.annotation.Id;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table( name = "alimento")
@Data
@NoArgsConstructor
public class Alimento implements Serializable{
	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String nombre;	
	private String estado;
	private double cal	;
	private double hidratosdecarbono	;
	private double h2o;	
	private double noespecifico;
	private double grasas	;
	private double proteinas;	
	private double vitamina;
	private double vitaminb2;	
	private double vitaminb1;
	private double vitaminc;	
	private double niac;	
	private double cobre;	
	private double potasio;	
	private double sodio;	
	private double azufre;	
	private double calcio;	
	private double fosforo;
	private double hierro;	
	private double magnesio;	
	private double cloro;	
	private double met; 	
	private double lis	;
	private double leu	;
	private double illeu;	
	private double tre;
	private double tri;	
	private double fen	;
	private double val;
	private double acid;
	private double alcal;
	

	
}
