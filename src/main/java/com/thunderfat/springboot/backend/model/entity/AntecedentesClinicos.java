package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "antecedentesclinicos")
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

	public AntecedentesClinicos() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getAntecedente() {
		return antecedente;
	}

	public void setAntecedente(String antecedente) {
		this.antecedente = antecedente;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Override
	public String toString() {
		return "AntecedentesClinicos [id=" + id + ", fecha=" + fecha + ", antecedente=" + antecedente + ", observacion="
				+ observacion + "]";
	}

}
