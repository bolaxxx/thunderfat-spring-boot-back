package com.thunderfat.springboot.backend.model.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="platoplandieta")
//@DiscriminatorValue("dieta")
public class PlatoPlanDieta extends Plato {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PlatoPlanDieta() {
		super();
	}

	@Override
	public String toString() {
		return "PlatoPlanDieta [getId()=" + getId() + ", getNombre()=" + getNombre() + ", getIngredientes()="
				+ getIngredientes() + ", getReceta()=" + getReceta() + ", getProteinastotales()="
				+ getProteinastotales() + ", getGrasastotales()=" + getGrasastotales() + ", getKcaltotales()="
				+ getKcaltotales() + ", getHidratostotales()=" + getHidratostotales() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}


	
}
