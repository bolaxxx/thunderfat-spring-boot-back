package com.thunderfat.springboot.backend.model.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="platopredeterminado")
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

	public PlatoPredeterminado() {
		super();
	}

	@Override
	public String toString() {
		return "PlatoPredeterminado [getId()=" + getId() + ", getNombre()=" + getNombre() + ", getIngredientes()="
				+ getIngredientes() + ", getReceta()=" + getReceta() + ", getProteinastotales()="
				+ getProteinastotales() + ", getGrasastotales()=" + getGrasastotales() + ", getKcaltotales()="
				+ getKcaltotales() + ", getHidratostotales()=" + getHidratostotales() + "]";
	}

	
	

	



	



}
