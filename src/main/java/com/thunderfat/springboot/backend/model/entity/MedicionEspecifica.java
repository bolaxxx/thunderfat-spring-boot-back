package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "medicion_especifica")
public class MedicionEspecifica implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private double grasavisceral;
	private double retencionliquidos;
	private double aguaid;
	private double litrosagua;
	private double porcentajeagua;
	private double musculoidmax;
	private double musculoidmin;
	private double mbi;
	private double metabolismo;
	private int edadmet;
	private double musculo;
	private double masaosea;
	private double poxmusmax;
	private double poxmusmin;
	private double porcentajegrasa;
	private double grasas;
	private double grasasidmin;
	private double grasaidmax;
	private double peso;
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate fecha;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getGrasavisceral() {
		return grasavisceral;
	}
	public void setGrasavisceral(double grasavisceral) {
		this.grasavisceral = grasavisceral;
	}
	public double getRetencionliquidos() {
		return retencionliquidos;
	}
	public void setRetencionliquidos(double retencionliquidos) {
		this.retencionliquidos = retencionliquidos;
	}
	public double getAguaid() {
		return aguaid;
	}
	public void setAguaid(double aguaid) {
		this.aguaid = aguaid;
	}
	public double getLitrosagua() {
		return litrosagua;
	}
	public void setLitrosagua(double litrosagua) {
		this.litrosagua = litrosagua;
	}
	public double getPorcentajeagua() {
		return porcentajeagua;
	}
	public void setPorcentajeagua(double porcentajeagua) {
		this.porcentajeagua = porcentajeagua;
	}
	public double getMusculoidmax() {
		return musculoidmax;
	}
	public void setMusculoidmax(double musculoidmax) {
		this.musculoidmax = musculoidmax;
	}
	public double getMusculoidmin() {
		return musculoidmin;
	}
	public void setMusculoidmin(double musculoidmin) {
		this.musculoidmin = musculoidmin;
	}
	public double getMbi() {
		return mbi;
	}
	public void setMbi(double mbi) {
		this.mbi = mbi;
	}
	public double getMetabolismo() {
		return metabolismo;
	}
	public void setMetabolismo(double metabolismo) {
		this.metabolismo = metabolismo;
	}
	public int getEdadmet() {
		return edadmet;
	}
	public void setEdadmet(int edadmet) {
		this.edadmet = edadmet;
	}
	public double getMusculo() {
		return musculo;
	}
	public void setMusculo(double musculo) {
		this.musculo = musculo;
	}
	public double getMasaosea() {
		return masaosea;
	}
	public void setMasaosea(double masaosea) {
		this.masaosea = masaosea;
	}
	public double getPoxmusmax() {
		return poxmusmax;
	}
	public void setPoxmusmax(double poxmusmax) {
		this.poxmusmax = poxmusmax;
	}
	public double getPoxmusmin() {
		return poxmusmin;
	}
	public void setPoxmusmin(double poxmusmin) {
		this.poxmusmin = poxmusmin;
	}
	public double getPorcentajegrasa() {
		return porcentajegrasa;
	}
	public void setPorcentajegrasa(double porcentajegrasa) {
		this.porcentajegrasa = porcentajegrasa;
	}
	public double getGrasas() {
		return grasas;
	}
	public void setGrasas(double grasas) {
		this.grasas = grasas;
	}
	public double getGrasasidmin() {
		return grasasidmin;
	}
	public void setGrasasidmin(double grasasidmin) {
		this.grasasidmin = grasasidmin;
	}
	public double getGrasaidmax() {
		return grasaidmax;
	}
	public void setGrasaidmax(double grasaidmax) {
		this.grasaidmax = grasaidmax;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	@Override
	public String toString() {
		return "MedicionEspecifica [id=" + id + ", grasavisceral=" + grasavisceral + ", retencionliquidos="
				+ retencionliquidos + ", aguaid=" + aguaid + ", litrosagua=" + litrosagua + ", porcentajeagua="
				+ porcentajeagua + ", musculoidmax=" + musculoidmax + ", musculoidmin=" + musculoidmin + ", mbi=" + mbi
				+ ", metabolismo=" + metabolismo + ", edadmet=" + edadmet + ", musculo=" + musculo + ", masaosea="
				+ masaosea + ", poxmusmax=" + poxmusmax + ", poxmusmin=" + poxmusmin + ", porcentajegrasa="
				+ porcentajegrasa + ", grasas=" + grasas + ", grasasidmin=" + grasasidmin + ", grasaidmax=" + grasaidmax
				+ ", peso=" + peso + ", fecha=" + fecha + "]";
	}
	

	
	//
	//

	// PesoOxMuscMax = M�sculo * 100 / 72
	// PesoOxMuscMin = M�sculo * 100 / 80
	
	

//	public void calcularMBI() {
//		if (this.paciente.getSexo().equals("hombre")) {
//			this.mbi = 66.5 + (13.75 * this.peso) + (500.3 * this.paciente.getAltura()) - (6.775 * this.calcularEdad());
//		} else {
//			this.mbi = 665.1 + (9.563 * this.peso) + (185 * (this.paciente.getAltura() * 100))
//					- (4.676 * this.calcularEdad());
//
//		}
//
//	}
//
//	public void calcularMusculoIdMax() {
//		if (this.paciente.getSexo().equals("hombre")) {
//			this.musculo_idmax = 0.78 * 23 * Math.pow(this.paciente.getAltura(), 2);
//		} else {
//			this.musculo_idmax = 0.72 * 22 * Math.pow(this.paciente.getAltura(), 2);
//		}
//	}
//
//	public void calcularMusculoIdMin() {
//		if (this.paciente.getSexo().equals("hombre")) {
//			this.musculo_idmin = 0.85 * 23 * Math.pow(this.paciente.getAltura(), 2);
//		} else {
//			this.musculo_idmin = 0.8 * 22 * Math.pow(this.paciente.getAltura(), 2);
//		}
//
//	}
//
//	public void calcularGrasaIdMin() {
//		if (this.paciente.getSexo().equals("hombre")) {
//			this.grasasid_min = 0.22 * 23 * Math.pow(this.paciente.getAltura(), 2);
//		} else {
//			this.grasasid_min = 0.28 * 22 * Math.pow(this.paciente.getAltura(), 2);
//		}
//	}
//
//	public void calcularGrasasIdMax() {
//		if (this.paciente.getSexo().equals("hombre")) {
//			this.grasaid_max = 0.15 * 23 * Math.pow(this.paciente.getAltura(), 2);
//		} else {
//
//			this.grasaid_max = 0.2 * 22 * Math.pow(this.paciente.getAltura(), 2);
//		}
//	}
//
//	public void calcularAguaId() {
//		if (this.paciente.getSexo().equals("hombre")) {
//			this.aguaid = 55 * 23 * Math.pow(this.paciente.getAltura(), 2) / 100;
//		} else {
//			this.aguaid = 50 * 22 * Math.pow(this.paciente.getAltura(), 2) / 100;
//			
//		}
//	}
//
//	public void calcularLitrosAgua() {
//		litrosagua = this.porcentajeagua * peso / 100;
//	}
//
//	public void calcularRetencionLiquidos() {
//		this.retencionliquidos = this.litrosagua - this.aguaid;
//	}
//
//	public void calcularPesoOxMuscMax() {
//		if (this.paciente.getSexo().equals("hombre")) {
//			this.poxmus_max = this.musculo * 100 / 78;
//		} else {
//			this.poxmus_max = this.musculo * 100 / 72;
//		}
//
//	}
//
//	public void calcularPesoOxMuscMin() {
//		if (this.paciente.getSexo().equals("hombre")) {
//			this.poxmus_min = this.musculo * 100 / 85;
//		} else {
//			this.poxmus_min = this.musculo * 100 / 80;
//		}
//	}
//
//	public void calcularGrasas() {
//		this.grasas = porcentajegrasa * peso / 100;
//	}
//
//	public int calcularEdad() {
//		int anonacimiento = this.paciente.getFechanacimiento().getYear();
//		int anoactual = LocalDate.now().getYear();
//		return anoactual - anonacimiento;
//	}

}
