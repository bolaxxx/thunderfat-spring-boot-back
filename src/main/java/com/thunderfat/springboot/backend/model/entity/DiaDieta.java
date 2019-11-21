package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
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
@Table(name="diadieta")
public class DiaDieta implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@DateTimeFormat(iso= ISO.DATE)
	private LocalDate fecha;
	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(nullable=true)
	@JsonIgnoreProperties({"hibernateLazyInitializer","hanlder"})
	private List<Comida> comidas;
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
	public List<Comida> getComidas() {
		return comidas;
	}
	public void setComidas(List<Comida> comidas) {
		this.comidas = comidas;
	}
	@Override
	public String toString() {
		return "DiaDieta [id=" + id + ", fecha=" + fecha + ", comidas=" + comidas + "]";
	}
	
	

}
