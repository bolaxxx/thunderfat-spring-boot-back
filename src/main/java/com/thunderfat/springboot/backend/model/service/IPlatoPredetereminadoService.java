package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

public interface IPlatoPredetereminadoService {

	PlatoPredeterminado buscarPorId(int id_plato);
	void eliminarPlatoPredeterminado(int id_plato);
	PlatoPredeterminado insertar(PlatoPredeterminado plato,int id_nutricionista );
	List<PlatoPredeterminado> listarPorNutricionista(int nutricionista);
	
	
}
