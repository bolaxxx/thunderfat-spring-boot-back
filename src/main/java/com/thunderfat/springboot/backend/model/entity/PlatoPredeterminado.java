package com.thunderfat.springboot.backend.model.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="platopredeterminado")
@Data
@NoArgsConstructor
//@DiscriminatorValue("predeterminado")
public class PlatoPredeterminado extends Plato{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	
//	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
//	private Nutricionista creador;

//	public Nutricionista getCreador() {
//		return creador;
//	}
//
//	public void setCreador(Nutricionista creador) {
//		this.creador = creador;
//	}

	

	



}
