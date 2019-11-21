package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity 
@Table(name="cita")
public class Cita implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private LocalDateTime fechaini;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private LocalDateTime fechafin;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_paciente")
	@JsonIgnoreProperties({"hibernateLazyInitializer","hanlder","citas","nutricionista"})
	private Paciente paciente;
	
	@ManyToOne(fetch=FetchType.LAZY )
	@JoinColumn(name="id_nutricionista")	
	@JsonIgnoreProperties({"hibernateLazyInitializer","hanlder","citas","pacientes"})
	private Nutricionista nutricionista;
	public Cita() {
		super();
		this.paciente= new Paciente();
		this.nutricionista= new Nutricionista();
	}







	public LocalDateTime getFechaini() {
		return fechaini;
	}



	public void setFechaini(LocalDateTime fechaini) {
		this.fechaini = fechaini;
	}



	public LocalDateTime getFechafin() {
		return fechafin;
	}



	public void setFechafin(LocalDateTime fechafin) {
		this.fechafin = fechafin;
	}



	public Paciente getPaciente() {
		return paciente;
	}



	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}



	


	


	public int getId() {
		return id;
	}







	public void setId(int id) {
		this.id = id;
	}







	@Override
	public String toString() {
		return "Cita [id=" + id + ", fechaini=" + fechaini + ", fechafin=" + fechafin + ", paciente="
				+ paciente.getId() + ", nutricionista=" + nutricionista.getId() + "]";
	}



	public Nutricionista getNutricionista() {
		return nutricionista;
	}



	public void setNutricionista(Nutricionista nutricionista) {
		this.nutricionista = nutricionista;
	}







	


	



	












	
	
}