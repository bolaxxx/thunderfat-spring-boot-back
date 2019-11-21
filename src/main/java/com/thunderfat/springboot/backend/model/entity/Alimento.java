package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="alimento")
public class Alimento implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String nombre;	
	private String estado;
	private double cal	;
	private double hidratosdecarbono	;
	private double h2o;	
	private double noespecifico;
	private double grasas	;
	private double proteinas;	
	private double vitamina;
	private double vitaminb2;	
	private double vitaminb1;
	private double vitaminc;	
	private double niac;	
	private double cobre;	
	private double potasio;	
	private double sodio;	
	private double azufre;	
	private double calcio;	
	private double fosforo;
	private double hierro;	
	private double magnesio;	
	private double cloro;	
	private double met; 	
	private double lis	;
	private double leu	;
	private double illeu;	
	private double tre;
	private double tri;	
	private double fen	;
	private double val;
	private double acid;
	private double alcal;
	
	public Alimento() {
		super();
	}
	
	

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public double getCal() {
		return cal;
	}
	public void setCal(double cal) {
		this.cal = cal;
	}

	public double getH2o() {
		return h2o;
	}
	public void setH2o(double h2o) {
		this.h2o = h2o;
	}
	
	public double getNoespecifico() {
		return noespecifico;
	}

	public void setNoespecifico(double noespecifico) {
		this.noespecifico = noespecifico;
	}

	public double getGrasas() {
		return grasas;
	}
	public void setGrasas(double grasas) {
		this.grasas = grasas;
	}
	public double getProteinas() {
		return proteinas;
	}
	public void setProteinas(double proteinas) {
		this.proteinas = proteinas;
	}
	public double getCobre() {
		return cobre;
	}
	public void setCobre(double cobre) {
		this.cobre = cobre;
	}
	public double getPotasio() {
		return potasio;
	}
	public void setPotasio(double potasio) {
		this.potasio = potasio;
	}
	public double getSodio() {
		return sodio;
	}
	public void setSodio(double sodio) {
		this.sodio = sodio;
	}
	public double getAzufre() {
		return azufre;
	}
	public void setAzufre(double azufre) {
		this.azufre = azufre;
	}
	public double getCalcio() {
		return calcio;
	}
	public void setCalcio(double calcio) {
		this.calcio = calcio;
	}
	public double getFosforo() {
		return fosforo;
	}
	public void setFosforo(double fosforo) {
		this.fosforo = fosforo;
	}
	public double getHierro() {
		return hierro;
	}
	public void setHierro(double hierro) {
		this.hierro = hierro;
	}
	public double getMagnesio() {
		return magnesio;
	}
	public void setMagnesio(double magnesio) {
		this.magnesio = magnesio;
	}
	public double getCloro() {
		return cloro;
	}
	public void setCloro(double cloro) {
		this.cloro = cloro;
	}
	public double getMet() {
		return met;
	}
	public void setMet(double met) {
		this.met = met;
	}
	public double getLis() {
		return lis;
	}
	public void setLis(double lis) {
		this.lis = lis;
	}
	public double getLeu() {
		return leu;
	}
	public void setLeu(double leu) {
		this.leu = leu;
	}
	public double getIlleu() {
		return illeu;
	}
	public void setIlleu(double illeu) {
		this.illeu = illeu;
	}
	public double getTre() {
		return tre;
	}
	public void setTre(double tre) {
		this.tre = tre;
	}
	public double getTri() {
		return tri;
	}
	public void setTri(double tri) {
		this.tri = tri;
	}
	public double getFen() {
		return fen;
	}
	public void setFen(double fen) {
		this.fen = fen;
	}
	public double getVal() {
		return val;
	}
	public void setVal(double val) {
		this.val = val;
	}


	





	public double getAcid() {
		return acid;
	}

	public void setAcid(double acid) {
		this.acid = acid;
	}

	public double getAlcal() {
		return alcal;
	}

	public void setAlcal(double alcal) {
		this.alcal = alcal;
	}


	public double getNiac() {
		return niac;
	}

	public void setNiac(double niac) {
		this.niac = niac;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public double getHidratosdecarbono() {
		return hidratosdecarbono;
	}



	public void setHidratosdecarbono(double hidratosdecarbono) {
		this.hidratosdecarbono = hidratosdecarbono;
	}



	public double getVitamina() {
		return vitamina;
	}



	public void setVitamina(double vitamina) {
		this.vitamina = vitamina;
	}



	public double getVitaminb2() {
		return vitaminb2;
	}



	public void setVitaminb2(double vitaminb2) {
		this.vitaminb2 = vitaminb2;
	}



	public double getVitaminb1() {
		return vitaminb1;
	}



	public void setVitaminb1(double vitaminb1) {
		this.vitaminb1 = vitaminb1;
	}



	public double getVitaminc() {
		return vitaminc;
	}



	public void setVitaminc(double vitaminc) {
		this.vitaminc = vitaminc;
	}



	@Override
	public String toString() {
		return "Alimento [id=" + id + ", nombre=" + nombre + ", estado=" + estado + ", cal=" + cal
				+ ", hidratosdecarbono=" + hidratosdecarbono + ", h2o=" + h2o + ", noespecifico=" + noespecifico
				+ ", grasas=" + grasas + ", proteinas=" + proteinas + ", vitamina=" + vitamina + ", vitaminb2="
				+ vitaminb2 + ", vitaminb1=" + vitaminb1 + ", vitaminc=" + vitaminc + ", niac=" + niac + ", cobre="
				+ cobre + ", potasio=" + potasio + ", sodio=" + sodio + ", azufre=" + azufre + ", calcio=" + calcio
				+ ", fosforo=" + fosforo + ", hierro=" + hierro + ", magnesio=" + magnesio + ", cloro=" + cloro
				+ ", met=" + met + ", lis=" + lis + ", leu=" + leu + ", illeu=" + illeu + ", tre=" + tre + ", tri="
				+ tri + ", fen=" + fen + ", val=" + val + ", acid=" + acid + ", alcal=" + alcal + "]";
	}

	
}
