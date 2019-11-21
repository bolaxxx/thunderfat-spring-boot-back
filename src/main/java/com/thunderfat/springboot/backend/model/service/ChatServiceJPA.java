package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thunderfat.springboot.backend.model.dao.ChatRepository;
import com.thunderfat.springboot.backend.model.entity.Chat;

@Service
public class ChatServiceJPA  implements IChatService{
	@Autowired
	private ChatRepository repo;

	@Override
	public Chat buscarPorId(int id_chat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void EliminarChat(int id_chat) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Chat buscarPorPaciente(int id_paciente) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Chat> BuscarPorNutricionista(int id_nutricionista) {
		// TODO Auto-generated method stub
		return null;
	}

}
