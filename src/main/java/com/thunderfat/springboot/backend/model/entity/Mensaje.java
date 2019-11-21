package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
@Entity
@Table(name="mensaje")
public class Mensaje implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue( strategy=GenerationType.IDENTITY)
	private int 	id_mensaje;
	
	private String contenido;
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private LocalDateTime timestamp;
	@ManyToOne
	@JoinColumn(name="id_emisor")
	private Usuario id_emisor	;
	
	public Mensaje() {
		super();
	}
	public int getId_mensaje() {
		return id_mensaje;
	}
	public void setId_mensaje(int id_mensaje) {
		this.id_mensaje = id_mensaje;
	}

	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public Usuario getId_emisor() {
		return id_emisor;
	}
	public void setId_emisor(Usuario id_emisor) {
		this.id_emisor = id_emisor;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "Mensaje [id_mensaje=" + id_mensaje + ",, contenido=" + contenido
				+ ", timestamp=" + timestamp + ", id_emisor=" + id_emisor + "]";
	}
	
	
	


}
