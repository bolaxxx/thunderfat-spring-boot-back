package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "filtroalimentario")
public class FiltroAlimentario implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String nombre;
	private String descripcion;


	/*
	 * @ManyToMany
	 * 
	 * @JoinTable(name="alimentos_en_filtro", joinColumns= @JoinColumn
	 * (name="id_filtroalimentario",referencedColumnName="id_filtroalimentario"),
	 * inverseJoinColumns=@JoinColumn(name="id_alimentofk",referencedColumnName=
	 * "id_alimento")) private List<Alimento>alimentos;
	 */

	public String getDescripcion() {
		return descripcion;
	}



	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}



//	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
//	@JoinTable(name = "alimentos_en_filtro", joinColumns = {
//			@JoinColumn(name = "id_filtroalimentario", referencedColumnName = "id") }, inverseJoinColumns = {
//					@JoinColumn(name = "id_alimentofk", referencedColumnName = "id") })
	@ManyToMany(fetch=FetchType.LAZY)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private List<Alimento> alimentos ;

	/*
	 * @ManyToMany(mappedBy="filtros_aplicados") private List<PlanDieta> planes;
	 */
	public FiltroAlimentario() {
		super();
		this.alimentos=new ArrayList<Alimento>();
	}

	

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	

	public List<Alimento> getAlimentos() {
		return alimentos;
	}

	public void setAlimentos(List<Alimento> alimentos) {
		this.alimentos = alimentos;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	@Override
	public String toString() {
		return "FiltroAlimentario [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", alimentos="
				+ alimentos + "]";
	}

	

	

}
