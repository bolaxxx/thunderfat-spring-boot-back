package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity 
@Table(name="cita")
@Data
@NoArgsConstructor
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
}