package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="diadieta")
@Data
@NoArgsConstructor
public class DiaDieta implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@DateTimeFormat(iso= ISO.DATE)
	private LocalDate fecha;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(nullable=true)
	@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
	private List<Comida> comidas;
	
	public DiaDieta(int id, LocalDate fecha, List<Comida> comidas) {
		super();
		this.id = id;
		this.fecha = fecha;
		this.comidas = comidas;
	}
}
