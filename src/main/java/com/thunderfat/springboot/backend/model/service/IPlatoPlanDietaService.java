package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;

public interface IPlatoPlanDietaService {
	PlatoPlanDieta buscarPorId(int id_plato);
	//List<PlatoPlanDieta> buscarPorNutricionista(Nutricionista nutricionista);
	void eliminar(int id_plato);
	void insertar(PlatoPlanDieta plato);

}
