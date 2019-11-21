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
@Table(name="medicion_general")
public class MedicionGeneral implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
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
	public double getPesoideal() {
		return pesoideal;
	}
	public void setPesoideal(double pesoideal) {
		this.pesoideal = pesoideal;
	}
	public double getPesoactual() {
		return pesoactual;
	}
	public void setPesoactual(double pesoactual) {
		this.pesoactual = pesoactual;
	}
	public double getBrazo() {
		return brazo;
	}
	public void setBrazo(double brazo) {
		this.brazo = brazo;
	}
	public double getIcc() {
		return icc;
	}
	public void setIcc(double icc) {
		this.icc = icc;
	}
	public double getPorcentajegrasas() {
		return porcentajegrasas;
	}
	public void setPorcentajegrasas(double porcentajegrasas) {
		this.porcentajegrasas = porcentajegrasas;
	}
	public double getImc() {
		return imc;
	}
	public void setImc(double imc) {
		this.imc = imc;
	}
	public double getTensionmin() {
		return tensionmin;
	}
	public void setTensionmin(double tensionmin) {
		this.tensionmin = tensionmin;
	}
	public double getTensionmax() {
		return tensionmax;
	}
	public void setTensionmax(double tensionmax) {
		this.tensionmax = tensionmax;
	}
	public double getCadera() {
		return cadera;
	}
	public void setCadera(double cadera) {
		this.cadera = cadera;
	}
	public double getCintura() {
		return cintura;
	}
	public void setCintura(double cintura) {
		this.cintura = cintura;
	}
	public MedicionGeneral() {
		super();
	}
	@Override
	public String toString() {
		return "MedicionGeneral [id=" + id + ", fecha=" + fecha + ", pesoideal=" + pesoideal + ", pesoactual="
				+ pesoactual + ", brazo=" + brazo + ", icc=" + icc + ", porcentajegrasas=" + porcentajegrasas + ", imc="
				+ imc + ", tensionmin=" + tensionmin + ", tensionmax=" + tensionmax + ", cadera=" + cadera
				+ ", cintura=" + cintura + "]";
	}

	
	 
	
	
}
