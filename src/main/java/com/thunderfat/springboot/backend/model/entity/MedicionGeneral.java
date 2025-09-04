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
@Table(name="medicion_general")
@Data
@NoArgsConstructor
public class MedicionGeneral implements Serializable {
    /**
     * 
     */
    @Serial
    private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(insertable=false, updatable=false)
	private int id_paciente;
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fecha;
	private double pesoideal;
	private double pesoactual;
	private double brazo;
	private double icc;
	private double porcentajegrasas;
	private double imc;
	private double tensionmin	;
	private double tensionmax	;
	private double cadera;
	private double cintura;
	private double muslo;
	private double pantorrilla;
	private double pecho;
	private double abdomen;
	private double cuello;
	
	 
	
	
}
