package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * @Entity
 *  tabla con todo los mensajes enviados por los usuarios a los profesionales
 * 
 */
@Entity
@Table(name = "mensaje")
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Mensaje implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id_mensaje;

	private String contenido;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime timestamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_emisor")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Usuario emisor;

	@Column(name = "leido")
	private boolean leido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_chat")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Chat chat;

}
