package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.NutricionistaRepository;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;

@Service
public class NutricionistaServiceJPA implements INutricionistaService {
	@Autowired
	private NutricionistaRepository repoNutricionista;

	public List<Nutricionista> listarNutricionista() {
		// TODO Auto-generated method stub
		return null;
	}

	public Nutricionista buscarPorId(int id_nutricionista) {
		Optional<Nutricionista>op=repoNutricionista.findById(id_nutricionista);
		// TODO Auto-generated method stub
		if(op.isPresent())
			return op.get();
		return null;
	}

	public void eliminar(Nutricionista nutricionista) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> buscarProvincias() {
		List <String>provincias= repoNutricionista.findDistinctProvincia();
		return provincias;
	}

	@Override
	public List<String> buscarLocalidadesporProvincia(String provincia) {
		List<String>localidades=repoNutricionista.findDistinctLocalidadByProvincia(provincia);
		return localidades;
	}

	@Override
	public List<Nutricionista> listarNutricionistaporlocalidad(String localidad) {
		List<Nutricionista>nutricionistas=repoNutricionista.findByLocalidad(localidad);
		return nutricionistas;
	}
	
	@Transactional
	public void guardar(Nutricionista nutri) {
		repoNutricionista.save(nutri);
	}
}
