package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "plato")
@Inheritance(strategy = InheritanceType.JOINED)
// @DiscriminatorColumn(name="type",discriminatorType=DiscriminatorType.STRING)
public class Plato  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String nombre;
	
	@OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="id_plato")
	private List<Ingrediente> ingredientes;
	@Lob
	@Column(columnDefinition = "TEXT")
	private String receta;
	private double proteinastotales;
	private double grasastotales;
	private double kcaltotales;
	private double hidratostotales;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<Ingrediente> getIngredientes() {
		return ingredientes;
	}
	public void setIngredientes(List<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}
	public String getReceta() {
		return receta;
	}
	public void setReceta(String receta) {
		this.receta = receta;
	}
	public double getProteinastotales() {
		return proteinastotales;
	}
	public void setProteinastotales(double proteinastotales) {
		this.proteinastotales = proteinastotales;
	}
	public double getGrasastotales() {
		return grasastotales;
	}
	public void setGrasastotales(double grasastotales) {
		this.grasastotales = grasastotales;
	}
	public double getKcaltotales() {
		return kcaltotales;
	}
	public void setKcaltotales(double kcaltotales) {
		this.kcaltotales = kcaltotales;
	}
	public double getHidratostotales() {
		return hidratostotales;
	}
	public void setHidratostotales(double hidratostotales) {
		this.hidratostotales = hidratostotales;
	}
	public Plato() {
		super();
		this.ingredientes= new ArrayList<Ingrediente>();
	}
	@Override
	public String toString() {
		return "Plato [id=" + id + ", nombre=" + nombre + ", ingredientes=" + ingredientes + ", receta=" + receta
				+ ", proteinastotales=" + proteinastotales + ", grasastotales=" + grasastotales + ", kcaltotales="
				+ kcaltotales + ", hidratostotales=" + hidratostotales + "]";
	}

}