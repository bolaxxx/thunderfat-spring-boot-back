package com.thunderfat.springboot.backend.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat")
@NoArgsConstructor
@Data
public class Chat implements Serializable {
	/**
	 * /**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	/**
	 *
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id_chat;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paciente")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Paciente paciente;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nutricionista")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private Nutricionista nutricionista;
	/**
	 * 
	 */
	private LocalDateTime fechahora;
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_chat")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private List<Mensaje> mensajes;

	public void addMensaje(Mensaje mensaje) {
		if (this.mensajes == null) {
			this.mensajes = new ArrayList<Mensaje>();
		}
		this.mensajes.add(mensaje);
	}
}
