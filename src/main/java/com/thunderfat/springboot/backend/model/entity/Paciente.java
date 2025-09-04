package com.thunderfat.springboot.backend.model.entity;



import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//declaracion de las anotaciones Para la persistenciaen base de datos indicamos que es una entidad y la tabla  a la que hace referencia 
@Entity
@Table(name = "paciente")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
//@JsonIdentityInfo(
//		  generator = ObjectIdGenerators.PropertyGenerator.class, 
//		  property = "id")
public class Paciente extends Usuario {
	// declaracion de los atributos de la clase
	
	private String nombre;
	private String apellidos;
	@DateTimeFormat(iso=ISO.DATE)
	private LocalDate fechanacimiento;
	private String direccion;
	private String localidad;
	private String codigopostal;
	private String provincia;
	
	@jakarta.validation.constraints.Pattern(regexp = "^[0-9]{8,8}[A-Za-z]$",message="dni no valido")
	private String dni;
	private double altura;
	private String telefono;
	private String sexo ;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_nutricionista")	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler","pacientes","citas"})
	private Nutricionista nutricionista;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy="paciente",orphanRemoval = true)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private List<Cita> citas;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="id_paciente",nullable=false)
	private List<MedicionEspecifica> medicionesespecificas;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="id_paciente", nullable=false)
	private List<MedicionGeneral> medicionesgenerales;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="id_paciente", nullable=false)
	private List<MedicionSegmental> medicionessegmentales;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="id_paciente", nullable=false)
	private List<AntecedentesClinicos> antecedentesclinicos;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_paciente",nullable=false)
	private List<AntecedenteTratamiento> antecedentestratamientos;
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="id_paciente", nullable=false)
	private List<PlanDieta> planesdieta;

	




}