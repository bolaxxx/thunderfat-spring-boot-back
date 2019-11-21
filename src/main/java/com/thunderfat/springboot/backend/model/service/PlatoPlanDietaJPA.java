package com.thunderfat.springboot.backend.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thunderfat.springboot.backend.model.dao.PlatoPlanDietaRepository;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;
@Service
public class PlatoPlanDietaJPA implements IPlatoPlanDietaService{
	@Autowired 
	private PlatoPlanDietaRepository repo;

	@Override
	public PlatoPlanDieta buscarPorId(int id_plato) {
		// TODO Auto-generated method stub
		repo.findById(id_plato).orElse(null);
		return repo.findById(id_plato).orElse(null);
	}

//	@Override
//	public List<PlatoPlanDieta> buscarPorNutricionista(Nutricionista nutricionista) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public void eliminar(int id_plato) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertar(PlatoPlanDieta plato) {
		// TODO Auto-generated method stub
		
	}

}
