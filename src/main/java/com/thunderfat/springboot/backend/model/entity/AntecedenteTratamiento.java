package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "antecedentetratamiento")
@Data
@NoArgsConstructor
public class AntecedenteTratamiento implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String antecedente;
	
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fecha;
	
	private String observacion;
	
	@ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Paciente paciente;

	public AntecedenteTratamiento(String antecedente, LocalDate fecha, String observacion) {
		super();
		this.antecedente = antecedente;
		this.fecha = fecha;
		this.observacion = observacion;
	}
}
