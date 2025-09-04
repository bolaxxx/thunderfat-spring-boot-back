package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "antecedentesclinicos")
@Data
@NoArgsConstructor
public class AntecedentesClinicos implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fecha;
	
	private String antecedente;
	private String observacion;
	private String descripcion;
	private String condicion;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Paciente paciente;

	public AntecedentesClinicos(LocalDate fecha, String antecedente, String observacion, String descripcion, String condicion) {
		super();
		this.fecha = fecha;
		this.antecedente = antecedente;
		this.observacion = observacion;
		this.descripcion = descripcion;
		this.condicion = condicion;
	}
}
