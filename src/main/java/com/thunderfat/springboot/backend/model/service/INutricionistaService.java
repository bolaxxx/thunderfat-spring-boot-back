package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.Nutricionista;

public interface INutricionistaService {
	
	List <Nutricionista> listarNutricionista();
	Nutricionista buscarPorId(int id_nutricionista);
	void eliminar(Nutricionista nutricionista);
	List <String>buscarProvincias();
	List <String>buscarLocalidadesporProvincia(String provincia);
	List<Nutricionista>listarNutricionistaporlocalidad(String localidad);
}
