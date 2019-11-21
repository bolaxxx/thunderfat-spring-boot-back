package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.Chat;

public interface IChatService {
	Chat buscarPorId(int id_chat);
	void EliminarChat(int id_chat);
	Chat buscarPorPaciente(int id_paciente);
	List <Chat>BuscarPorNutricionista(int id_nutricionista);

}
