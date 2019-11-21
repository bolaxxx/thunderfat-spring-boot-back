package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "comidas")
public class Comida implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	
	//@DateTimeFormat(iso= ISO.DATE)
	//private LocalDate fecha;
	@DateTimeFormat(iso=ISO.TIME)
	private LocalTime hora;
	private int  valoracion;
	
	//private double cantidad;
	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn
	@JsonIgnoreProperties({"hibernateLazyInitializer","hanlder"})
	private List<PlatoPlanDieta> platos;
	
	public Comida() {
		super();
		this.platos=new ArrayList<PlatoPlanDieta>();
	}







//	public LocalDate getFecha() {
//		return fecha;
//	}
//
//
//
//
//
//	public void setFecha(LocalDate fecha) {
//		this.fecha = fecha;
//	}









	




	public int getId() {
		return id;
	}







	public void setId(int id) {
		this.id = id;
	}







	public int getValoracion() {
		return valoracion;
	}





	public void setValoracion(int valoracion) {
		this.valoracion = valoracion;
	}





	




	public LocalTime getHora() {
		return hora;
	}





	public void setHora(LocalTime hora) {
		this.hora = hora;
	}







	public List<PlatoPlanDieta> getPlatos() {
		return platos;
	}







	public void setPlatos(List<PlatoPlanDieta> platos) {
		this.platos = platos;
	}







	@Override
	public String toString() {
		return "Comida [id=" + id + ", hora=" + hora + ", valoracion=" + valoracion + ", platos=" + platos + "]";
	}












//	@Override
//	public String toString() {
//		return "Comida [id=" + id + ", fecha=" + fecha + ", hora=" + hora + ", valoracion=" + valoracion + ", platos="
//				+ platos + "]";
//	}
//






	







}
