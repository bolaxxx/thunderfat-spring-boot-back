package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="nutricionista")
@JsonIdentityInfo(
	  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
public class Nutricionista extends Usuario implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private String apellidos;
	private String telefono;
	private String localidad;
	private String provincia;
	private String dni;
	private String direccion;
	 
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="nutricionista",orphanRemoval = true)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","nutricionista"})
	private List<Paciente> pacientes;
	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,mappedBy="nutricionista")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private List<Cita> citas;
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="id_nutricionista",nullable=false)
	private List<PlanDieta> planesdietas;
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="id_nutricionista",nullable=false)
	private List<FiltroAlimentario>filtros;
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@JoinColumn(name="id_nutricionista",nullable=false)
	private List<PlatoPredeterminado> platos;
	public Nutricionista() {
		super();
		this.citas=new ArrayList<Cita>();
		this.filtros=new ArrayList<FiltroAlimentario>();
		this.planesdietas=new ArrayList<PlanDieta>();
		this.pacientes=new ArrayList<Paciente>();
		this.platos=new ArrayList<PlatoPredeterminado>();
		
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getLocalidad() {
		return localidad;
	}
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
//	public Set<Paciente> getPacientes() {
//		return pacientes;
//	}
//	public void setPacientes(Set<Paciente> pacientes) {
//		this.pacientes = pacientes;
//	}
//	public Set<Cita> getCitas() {
//		return citas;
//	}
//	public void setCitas(Set<Cita> citas) {
//		this.citas = citas;
//	}
//	public Set<PlanDieta> getPlanesdietas() {
//		return planesdietas;
//	}
//	public void setPlanesdietas(Set<PlanDieta> planesdietas) {
//		this.planesdietas = planesdietas;
//	}
//	public Set<FiltroAlimentario> getFiltros() {
//		return filtros;
//	}
//	public void setFiltros(Set<FiltroAlimentario> filtros) {
//		this.filtros = filtros;
//	}
//	@Override
//	public String toString() {
//		return "Nutricionista [id_nutricionista=" + id_nutricionista + ", nombre=" + nombre + ", apellidos=" + apellidos
//				+ ", telefono=" + telefono + ", localidad=" + localidad + ", provincia=" + provincia + ", dni=" + dni
//				+ "]";
//	}
//	
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public List<Paciente> getPacientes() {
		return pacientes;
	}

	public void setPacientes(List<Paciente> pacientes) {
		this.pacientes = pacientes;
	}

	public List<Cita> getCitas() {
		return citas;
	}

	public void setCitas(List<Cita> citas) {
		this.citas = citas;
	}

	public List<PlanDieta> getPlanesdietas() {
		return planesdietas;
	}

	public void setPlanesdietas(List<PlanDieta> planesdietas) {
		this.planesdietas = planesdietas;
	}

	public List<FiltroAlimentario> getFiltros() {
		return filtros;
	}

	public void setFiltros(List<FiltroAlimentario> filtros) {
		this.filtros = filtros;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<PlatoPredeterminado> getPlatos() {
		return platos;
	}

	public void setPlatos(List<PlatoPredeterminado> platos) {
		this.platos = platos;
	}

	@Override
	public String toString() {
		return "Nutricionista [nombre=" + nombre + ", apellidos=" + apellidos + ", telefono=" + telefono
				+ ", localidad=" + localidad + ", provincia=" + provincia + ", dni=" + dni + ", direccion=" + direccion
				+ ", pacientes=" + pacientes + ", citas=" + citas + ", planesdietas=" + planesdietas + ", filtros="
				+ filtros + ", platos=" + platos + ", getRoles()=" + getRoles() + ", getEmail()=" + getEmail()
				+ ", getPsw()=" + getPsw() + ", getId()=" + getId() + "]";
	}
	
	
	 public void addPaciente(Paciente comment) {
	        pacientes.add(comment);
	        comment.setNutricionista(this);
	    }
	 
	    public void removePaciente( Paciente comment) {
	        pacientes.remove(comment);
	        comment.setNutricionista(null);
	    }

}
