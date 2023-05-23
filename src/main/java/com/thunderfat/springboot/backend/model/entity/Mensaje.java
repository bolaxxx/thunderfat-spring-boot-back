package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @Entity
 *  tabla con todo los mensajes enviados por los usuarios a los profesionales
 * 
 */

@Entity
@Table(name="mensaje")
@Data
@NoArgsConstructor
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
	

	
	


}
