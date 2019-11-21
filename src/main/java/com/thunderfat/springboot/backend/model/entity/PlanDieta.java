package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity	
@Table(name="planDieta")
public class PlanDieta implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;	
	
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="id_paciente",nullable=false)
//	private Paciente paciente;
//	
	
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fechaini;
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fechafin;
	private double calrangomin;
	private double calrangomax;
	private double ingestacaldiaria;
	private double repartoglucidodiario;
	private double repartolipidodiario;
	private double repartoprotidodiario	;
	private int comidasdiarias;
	private short visible;
	private short intercambiable;
	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="id_plandieta",nullable=false)
	@JsonIgnoreProperties({"hibernateLazyInitializer","hanlder"})
	private List<DiaDieta> dias;
	
//	@ManyToMany
//	@JoinTable(name="filtros_aplicados",
//	joinColumns=@JoinColumn (name="id_plandieta",referencedColumnName="id_plandieta"),
//	inverseJoinColumns=@JoinColumn(name="id_filtroalimentario",referencedColumnName="id_filtroalimentario"))
	@ManyToOne
	@JoinColumn(name="filtro")
	private FiltroAlimentario filtrosaplicado;
	
	
	
	public PlanDieta() {
		super();
		this.dias=new ArrayList<>();
	}



	







	@Override
	public String toString() {
		return "PlanDieta [id=" + id + ", fechaini=" + fechaini + ", fechafin=" + fechafin + ", calrangomin="
				+ calrangomin + ", calrangomax=" + calrangomax + ", ingestacaldiaria=" + ingestacaldiaria
				+ ", repartoglucidodiario=" + repartoglucidodiario + ", repartolipidodiario=" + repartolipidodiario
				+ ", repartoprotidodiario=" + repartoprotidodiario + ", comidasdiarias=" + comidasdiarias + ", visible="
				+ visible + ", intercambiable=" + intercambiable + ", dias=" + dias + ", filtrosaplicado="
				+ filtrosaplicado + "]";
	}











	public int getId() {
		return id;
	}











	public void setId(int id) {
		this.id = id;
	}











	public LocalDate getFechaini() {
		return fechaini;
	}



	public void setFechaini(LocalDate fechaini) {
		this.fechaini = fechaini;
	}



	public LocalDate getFechafin() {
		return fechafin;
	}



	public void setFechafin(LocalDate fechafin) {
		this.fechafin = fechafin;
	}



	public double getCalrangomin() {
		return calrangomin;
	}



	public void setCalrangomin(double calrangomin) {
		this.calrangomin = calrangomin;
	}



	public double getCalrangomax() {
		return calrangomax;
	}



	public void setCalrangomax(double calrangomax) {
		this.calrangomax = calrangomax;
	}



	public double getIngestacaldiaria() {
		return ingestacaldiaria;
	}



	public void setIngestacaldiaria(double ingestacaldiaria) {
		this.ingestacaldiaria = ingestacaldiaria;
	}



	public double getRepartoglucidodiario() {
		return repartoglucidodiario;
	}



	public void setRepartoglucidodiario(double repartoglucidodiario) {
		this.repartoglucidodiario = repartoglucidodiario;
	}



	public double getRepartolipidodiario() {
		return repartolipidodiario;
	}



	public void setRepartolipidodiario(double repartolipidodiario) {
		this.repartolipidodiario = repartolipidodiario;
	}



	public double getRepartoprotidodiario() {
		return repartoprotidodiario;
	}



	public void setRepartoprotidodiario(double repartoprotidodiario) {
		this.repartoprotidodiario = repartoprotidodiario;
	}



	public int getComidasdiarias() {
		return comidasdiarias;
	}



	public void setComidasdiarias(int comidasdiarias) {
		this.comidasdiarias = comidasdiarias;
	}



	public short getVisible() {
		return visible;
	}



	public void setVisible(short visible) {
		this.visible = visible;
	}



	public short getIntercambiable() {
		return intercambiable;
	}



	public void setIntercambiable(short intercambiable) {
		this.intercambiable = intercambiable;
	}





	public List<DiaDieta> getDias() {
		return dias;
	}



	public void setDias(List<DiaDieta> dias) {
		this.dias = dias;
	}



	public FiltroAlimentario getFiltrosaplicado() {
		return filtrosaplicado;
	}



	public void setFiltrosaplicado(FiltroAlimentario filtrosaplicado) {
		this.filtrosaplicado = filtrosaplicado;
	}





	



	

	
	





	
	
	

}
