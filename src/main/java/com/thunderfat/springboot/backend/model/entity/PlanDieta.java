package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity	
@Table(name="planDieta")
@NoArgsConstructor
@Data
public class PlanDieta implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;	
	
	@Column(insertable=false, updatable=false)
	private int id_paciente;
	@Column(insertable=false, updatable=false)
	private int id_nutricionista;
	
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
	private double repartoprotidodiario;
	private int comidasdiarias;
	private short visible;
	private short intercambiable;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="id_plandieta",nullable=false)
	@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
	private List<DiaDieta> dias;
	
//	@ManyToMany
//	@JoinTable(name="filtros_aplicados",
//	joinColumns=@JoinColumn (name="id_plandieta",referencedColumnName="id_plandieta"),
//	inverseJoinColumns=@JoinColumn(name="id_filtroalimentario",referencedColumnName="id_filtroalimentario"))
	@ManyToOne
	@JoinColumn(name="filtro")
	private FiltroAlimentario filtrosaplicado;
}
