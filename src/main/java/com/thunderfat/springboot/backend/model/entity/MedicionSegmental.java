/**
 * 
 */
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

/**
 * @author sergio
 *
 */
@Entity
@Table(name="medicion_segmental")
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
	public double getBdporcentajegrasas() {
		return bdporcentajegrasas;
	}
	public void setBdporcentajegrasas(double bdporcentajegrasas) {
		this.bdporcentajegrasas = bdporcentajegrasas;
	}
	public double getBdmusculo() {
		return bdmusculo;
	}
	public void setBdmusculo(double bdmusculo) {
		this.bdmusculo = bdmusculo;
	}
	public double getBimusculo() {
		return bimusculo;
	}
	public void setBimusculo(double bimusculo) {
		this.bimusculo = bimusculo;
	}
	public double getPiporcentajegrasas() {
		return piporcentajegrasas;
	}
	public void setPiporcentajegrasas(double piporcentajegrasas) {
		this.piporcentajegrasas = piporcentajegrasas;
	}
	public double getPdmusculo() {
		return pdmusculo;
	}
	public void setPdmusculo(double pdmusculo) {
		this.pdmusculo = pdmusculo;
	}
	public double getPdporcentajegrasas() {
		return pdporcentajegrasas;
	}
	public void setPdporcentajegrasas(double pdporcentajegrasas) {
		this.pdporcentajegrasas = pdporcentajegrasas;
	}
	public double getTporcentajegrasa() {
		return tporcentajegrasa;
	}
	public void setTporcentajegrasa(double tporcentajegrasa) {
		this.tporcentajegrasa = tporcentajegrasa;
	}
	public double getTmusculo() {
		return tmusculo;
	}
	public void setTmusculo(double tmusculo) {
		this.tmusculo = tmusculo;
	}
	public double getPimusculo() {
		return pimusculo;
	}
	public void setPimusculo(double pimusculo) {
		this.pimusculo = pimusculo;
	}
	public double getBiporcentajegrasas() {
		return biporcentajegrasas;
	}
	public void setBiporcentajegrasas(double biporcentajegrasas) {
		this.biporcentajegrasas = biporcentajegrasas;
	}
	@Override
	public String toString() {
		return "MedicionSegmental [id=" + id + ", fecha=" + fecha + ", bdporcentajegrasas=" + bdporcentajegrasas
				+ ", bdmusculo=" + bdmusculo + ", bimusculo=" + bimusculo + ", piporcentajegrasas=" + piporcentajegrasas
				+ ", pdmusculo=" + pdmusculo + ", pdporcentajegrasas=" + pdporcentajegrasas + ", tporcentajegrasa="
				+ tporcentajegrasa + ", tmusculo=" + tmusculo + ", pimusculo=" + pimusculo + ", biporcentajegrasas="
				+ biporcentajegrasas + "]";
	}
	public MedicionSegmental() {
		super();
	}	
	
	
	
		
}
