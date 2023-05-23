package com.thunderfat.springboot.backend.model.entity;



import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="platoplandieta")
@Data
@NoArgsConstructor
public class PlatoPlanDieta extends Plato {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
}
