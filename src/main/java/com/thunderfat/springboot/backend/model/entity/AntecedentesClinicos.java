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
@Table(name = "antecedentesclinicos")
@Data
@NoArgsConstructor
public class AntecedentesClinicos implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fecha;
	private String antecedente;
	private String observacion;
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name = "id_paciente")
//	private Paciente paciente;

	public AntecedentesClinicos(LocalDate fecha, String antecedente, String observacion) {
		super();
		this.fecha = fecha;
		this.antecedente = antecedente;
		this.observacion = observacion;
	}

}
