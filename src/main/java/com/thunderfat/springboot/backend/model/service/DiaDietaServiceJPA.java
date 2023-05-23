package com.thunderfat.springboot.backend.model.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.DiaDietaRepository;
import com.thunderfat.springboot.backend.model.entity.DiaDieta;
@Service
public class DiaDietaServiceJPA implements IDIaDietaService {

@Autowired
private DiaDietaRepository repo;

	@Override
	@Transactional()
	public DiaDieta insertar(DiaDieta dia) {
		// TODO Auto-generated method stub
		repo.save(dia);
		return dia;
	}

	@Override
	@Transactional(readOnly=true)
	public DiaDieta buscarporID(int id) {
		// TODO Auto-generated method stub
		return this.repo.findById(id).orElse(null);
	}

	@Override
	public List<DiaDieta> buscarporPlan(int id_plan) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eliminar(int id_dia) {
		// TODO Auto-generated method stub
		
	}

}
