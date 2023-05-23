/**
 * 
 */
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

/**
 * @author sergio
 *
 */
@Entity
@Table(name="medicion_segmental")
@NoArgsConstructor
@Data
public class MedicionSegmental implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fecha;
	private double bdporcentajegrasas;	
	private double bdmusculo;	
	private double bimusculo;	
	private double piporcentajegrasas;	
	private double pdmusculo;	
	private double pdporcentajegrasas;	
	private double tporcentajegrasa;	
	private double tmusculo;	
	private double pimusculo;	
	private double biporcentajegrasas;

	
		
}
