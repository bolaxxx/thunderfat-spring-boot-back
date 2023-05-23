package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "filtroalimentario")
@NoArgsConstructor
@Data
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
	

}
