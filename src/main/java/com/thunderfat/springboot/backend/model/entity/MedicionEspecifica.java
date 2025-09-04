package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medicion_especifica")
@NoArgsConstructor
@Data
public class MedicionEspecifica implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(insertable=false, updatable=false)
	private int id_paciente;
	
	private double grasavisceral;
	private double retencionliquidos;
	private double aguaid;
	private double litrosagua;
	private double porcentajeagua;
	private double musculoidmax;
	private double musculoidmin;
	private double mbi;
	private double metabolismo;
	private int edadmet;
	private double musculo;
	private double masaosea;
	private double poxmusmax;
	private double poxmusmin;
	private double porcentajegrasa;
	private double grasas;
	private double grasasidmin;
	private double grasaidmax;
	private double peso;
	
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate fecha;
}
