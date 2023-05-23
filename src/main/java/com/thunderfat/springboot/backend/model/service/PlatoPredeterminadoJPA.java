package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.PlatoPredeterminadoRepository;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

@Service
public class PlatoPredeterminadoJPA implements IPlatoPredetereminadoService {
@Autowired
private PlatoPredeterminadoRepository repo;
@Autowired
private NutricionistaServiceJPA serviceNutricionista;
@Override
public PlatoPredeterminado buscarPorId(int id_plato) {
	
	Optional <PlatoPredeterminado>op=repo.findById(id_plato);
	if(op.isPresent())
		return op.get();
	return null;
}

@Override
public void eliminarPlatoPredeterminado(int id_plato) {
	
	repo.deleteById(id_plato);
	
}

@Override
@Transactional
public PlatoPredeterminado insertar(PlatoPredeterminado plato,int id_nutricionista) {
	Nutricionista nutri =this.serviceNutricionista.buscarPorId(id_nutricionista);
	nutri.getPlatos().add(plato);
	this.serviceNutricionista.guardar(nutri);
	//repo.save(plato);
	
	
	return nutri.getPlatos().get(nutri.getPlatos().size()-1);
}

@Override
@Transactional(readOnly=true)
public List<PlatoPredeterminado> listarPorNutricionista(int nutricionista) {
	return repo.listapornutricionista(nutricionista);
}



}
