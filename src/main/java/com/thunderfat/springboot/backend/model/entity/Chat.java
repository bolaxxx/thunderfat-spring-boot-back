package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

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
@Table(name="chat")
@NoArgsConstructor
@Data
public class Chat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 *
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id_chat;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="paciente")
	private Paciente paciente;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn (name ="nutricionista")
	private Nutricionista nutricionista;
	
/**
	 *
	 */
	@OneToMany(fetch=FetchType.LAZY)	
	@JoinColumn(name="id_chat")
	private List<Mensaje> mensajes;
	
	public void addMensaje(Mensaje mensaje) {
		if(this.mensajes==null) {
			this.mensajes= new ArrayList<Mensaje>();
		}
		this.mensajes.add(mensaje);
	}
}
