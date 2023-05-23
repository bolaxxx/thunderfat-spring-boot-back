package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "antecedentetratamiento")
@Data
@NoArgsConstructor
public class AntecedenteTratamiento implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String antecedente;
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fecha;
	private String observacion;

	public AntecedenteTratamiento(String antecedente, LocalDate fecha, String observacion) {
		super();
		this.antecedente = antecedente;
		this.fecha = fecha;
		this.observacion = observacion;
	}

}
