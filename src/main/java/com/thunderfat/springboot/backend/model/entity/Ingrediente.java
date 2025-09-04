package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ingredientes")
@Data
@NoArgsConstructor
public class Ingrediente implements Serializable {
    /**
     * 
     */
    @Serial
    private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(nullable=false )
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Alimento alimento;
	
	private double  cantidad;
	
	
	private double proteinastotales;
	private double grasastotales;
	private double kcaltotales;
	private double hidratostotales;
	
	
	
	
	

}
