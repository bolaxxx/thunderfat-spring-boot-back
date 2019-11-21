package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//declaracion de las anotaciones Para la persistenciaen base de datos indicamos que es una entidad y la tabla  a la que hace referencia 
@Entity
@Table(name = "paciente")
//@JsonIdentityInfo(
//		  generator = ObjectIdGenerators.PropertyGenerator.class, 
//		  property = "id")
public class Paciente extends Usuario implements Serializable{
	// declaracion de los atributos de la clase
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;




	private String nombre;
	private String apellidos;
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fechanacimiento;
	private String direccion;
	private String localidad;
	private String codigopostal;
	private String provincia;
	 @Pattern(regexp = "^[0-9]{8,8}[A-Za-z]$",message="dni no valido")
	 
	private String dni;
	private double altura;
	private String telefono;
	private String sexo ;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_nutricionista")	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","pacientes","citas"})
	private Nutricionista nutricionista;
	
	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="paciente",orphanRemoval = true)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private List<Cita> citas;

	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="id_paciente",nullable=false)
	private List<MedicionEspecifica> medicionesespecificas;

	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="id_paciente", nullable=false)
	private List<MedicionGeneral> medicionesgenerales;

	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="id_paciente", nullable=false)
	private List<MedicionSegmental> medicionessegmentales;
	
	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="id_paciente", nullable=false)
	private List<AntecedentesClinicos> antecedentesclinicos;

	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name = "id_paciente",nullable=false)
	private List<AntecedenteTratamiento> antecedentestratamientos;
	@OneToMany(fetch = FetchType.LAZY,cascade=CascadeType.ALL,orphanRemoval = true)
	@JoinColumn(name="id_paciente", nullable=false)
	private List<PlanDieta> planesdieta;

	

	// Constructor
	public Paciente() {
		super();
		this.nutricionista= new Nutricionista();
		this.medicionesgenerales=new ArrayList<MedicionGeneral>();
		this.medicionesespecificas=new ArrayList<MedicionEspecifica>();
		this.medicionessegmentales=new ArrayList<MedicionSegmental>();
		this.citas=new ArrayList<Cita>();
		this.antecedentesclinicos=new ArrayList<AntecedentesClinicos>();
		this.antecedentestratamientos=new ArrayList<AntecedenteTratamiento>();
		this.planesdieta=new ArrayList<PlanDieta>();
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



	public LocalDate getFechanacimiento() {
		return fechanacimiento;
	}



	public void setFechanacimiento(LocalDate fechanacimiento) {
		this.fechanacimiento = fechanacimiento;
	}



	public String getDireccion() {
		return direccion;
	}



	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}



	public String getLocalidad() {
		return localidad;
	}



	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}



	public String getCodigopostal() {
		return codigopostal;
	}



	public void setCodigopostal(String codigopostal) {
		this.codigopostal = codigopostal;
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



	public double getAltura() {
		return altura;
	}



	public void setAltura(double altura) {
		this.altura = altura;
	}



	public String getTelefono() {
		return telefono;
	}



	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}



	public String getSexo() {
		return sexo;
	}



	public void setSexo(String sexo) {
		this.sexo = sexo;
	}



	public List<Cita> getCitas() {
		return citas;
	}



	public void setCitas(List<Cita> citas) {
		this.citas = citas;
	}



	public List<MedicionEspecifica> getMedicionesespecificas() {
		return medicionesespecificas;
	}



	public void setMedicionesespecificas(List<MedicionEspecifica> medicionesespecificas) {
		this.medicionesespecificas = medicionesespecificas;
	}



	public List<MedicionGeneral> getMedicionesgenerales() {
		return medicionesgenerales;
	}



	public void setMedicionesgenerales(List<MedicionGeneral> medicionesgenerales) {
		this.medicionesgenerales = medicionesgenerales;
	}



	public List<MedicionSegmental> getMedicionessegmentales() {
		return medicionessegmentales;
	}



	public void setMedicionessegmentales(List<MedicionSegmental> medicionessegmentales) {
		this.medicionessegmentales = medicionessegmentales;
	}



	public List<AntecedentesClinicos> getAntecedentesclinicos() {
		return antecedentesclinicos;
	}



	public void setAntecedentesclinicos(List<AntecedentesClinicos> antecedentesclinicos) {
		this.antecedentesclinicos = antecedentesclinicos;
	}



	public List<AntecedenteTratamiento> getAntecedentestratamientos() {
		return antecedentestratamientos;
	}



	public void setAntecedentestratamientos(List<AntecedenteTratamiento> antecedentestratamientos) {
		this.antecedentestratamientos = antecedentestratamientos;
	}



	public List<PlanDieta> getPlanesdieta() {
		return planesdieta;
	}



	public void setPlanesdieta(List<PlanDieta> planesdieta) {
		this.planesdieta = planesdieta;
	}



	@Override
	public String toString() {
		return "Paciente [nombre=" + nombre + ", apellidos=" + apellidos + ", fechanacimiento=" + fechanacimiento
				+ ", direccion=" + direccion + ", localidad=" + localidad + ", codigopostal=" + codigopostal
				+ ", provincia=" + provincia + ", dni=" + dni + ", altura=" + altura + ", telefono=" + telefono
				+ ", sexo=" + sexo +  ", citas=" + citas
				+ ", medicionesespecificas=" + medicionesespecificas + ", medicionesgenerales=" + medicionesgenerales
				+ ", medicionessegmentales=" + medicionessegmentales + ", antecedentesclinicos=" + antecedentesclinicos
				+ ", antecedentestratamientos=" + antecedentestratamientos + ", planesdieta=" + planesdieta + "]";
	}



	public Nutricionista getNutricionista() {
		return nutricionista;
	}



	public void setNutricionista(Nutricionista nutricionista) {
		this.nutricionista = nutricionista;
	}
//
//	
//
//	


	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}



	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
	

}
