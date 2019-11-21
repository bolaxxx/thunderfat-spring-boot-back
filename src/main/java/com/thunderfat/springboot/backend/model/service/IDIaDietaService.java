package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.DiaDieta;

public interface IDIaDietaService {
	DiaDieta insertar(DiaDieta dia);
	DiaDieta buscarporID(int id);
	List<DiaDieta>buscarporPlan(int id_plan);
	void eliminar(int id_dia);

}
