package com.thunderfat.springboot.backend.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
@Entity
@Table(name="chat")
public class Chat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id_chat;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="paciente")
	private Paciente paciente;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn (name ="nutricionista")
	private Nutricionista nutricionista;
	

	
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)	
	@JoinColumn(name="id_chat")
	private List<Mensaje> mensajes;
	public Chat() {
		super();
		this.mensajes= new ArrayList<Mensaje>(); 
		}
	public int getId_chat() {
		return id_chat;
	}
	public void setId_chat(int id_chat) {
		this.id_chat = id_chat;
	}
	
	
	public Paciente getPaciente() {
		return paciente;
	}
	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}
	public Nutricionista getNutricionista() {
		return nutricionista;
	}
	public void setNutricionista(Nutricionista nutricionista) {
		this.nutricionista = nutricionista;
	}
	public List<Mensaje> getMensajes() {
		return mensajes;
	}
	public void setMensajes(List<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}
	@Override
	public String toString() {
		return "Chat [id_chat=" + id_chat + ", paciente=" + paciente + ", nutricionista=" + nutricionista
				+ ", mensajes=" + mensajes + "]";
	}
	
	
}
