package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comidas")
@Data
@NoArgsConstructor
public class Comida implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	//@DateTimeFormat(iso= ISO.DATE)
	//private LocalDate fecha;
	@DateTimeFormat(iso=ISO.TIME)
	private LocalTime hora;
	
	private int valoracion;
	
	//private double cantidad;
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn
	@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
	private List<PlatoPlanDieta> platos;

	public Comida(int id, LocalTime hora, int valoracion, List<PlatoPlanDieta> platos) {
		super();
		this.id = id;
		this.hora = hora;
		this.valoracion = valoracion;
		this.platos = platos;
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
}
