package com.thunderfat.springboot.backend.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;

public interface IFiltroAlimentarioService {
	
	List<FiltroAlimentario> listarporNutricionista(int  id_nutricionista);
	FiltroAlimentario buscarPorId(int id_filtroalimentario);
	void insertar(FiltroAlimentario filtroalimentario,int id_nutricionista);
	void eliminar(int id_filtroalimentario);
	List<FiltroAlimentario> listarTodos();
	ArrayList<Map> alimentosEnFiltro(int id_filtro);
	int InsetarAjax(Map filtrojson);

}
