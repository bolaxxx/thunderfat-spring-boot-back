package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thunderfat.springboot.backend.model.dao.MensajeRepository;
import com.thunderfat.springboot.backend.model.entity.Mensaje;

@Service
public class MensajeServiceJPA implements IMensajeService {
	@Autowired
	private MensajeRepository repo;

	@Override
	public Mensaje buscarPorId(int id_mensaje) {
		// TODO Auto-generated method stub
		if(repo.findById(id_mensaje).isPresent())
			return repo.findById(id_mensaje).get();
		return null;
	}

	@Override
	public List<Mensaje> buscarPorChat(int id_chat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eliminarPorId(int id_mensaje) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eliminarPorChat(int id_chat) {
		// TODO Auto-generated method stub
		
	}

}
