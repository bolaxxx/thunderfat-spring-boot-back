package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.Mensaje;

public interface IMensajeService {
	Mensaje buscarPorId(int id_mensaje);
	List<Mensaje>buscarPorChat(int id_chat);
	void eliminarPorId(int id_mensaje);
	void eliminarPorChat(int id_chat);
	

}
