package com.thunderfat.springboot.backend.model.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.AlimentoRepository;
import com.thunderfat.springboot.backend.model.entity.Alimento;

@Service
public class AlimentoServiceJPA implements IAlimentoService {
	@Autowired
	private AlimentoRepository alimentorepo;

	@Transactional(readOnly = true)
	public List<Alimento> listarAlimentos() {
		return alimentorepo.findAll();
	}

	@Transactional
	public void insertar(Alimento alimento) {
		alimentorepo.save(alimento);
	}

	@Transactional(readOnly = true)
	public Alimento buscarPorId(int id_alimento) {
		Optional<Alimento> optional = alimentorepo.findById(id_alimento);
		return optional.orElse(null);
	}

	@Transactional
	public void eliminar(int id_alimento) {
		alimentorepo.deleteById(id_alimento);
	}

	@Override
	public ArrayList<Map<String, Object>> listarParaSelect() {
		List<Alimento> alimentos = alimentorepo.findAll();
		ArrayList<Map<String, Object>> result = new ArrayList<>();
		for (Alimento alimento : alimentos) {
			Map<String, Object> temp = new LinkedHashMap<>();
			temp.put("id", alimento.getId());
			temp.put("text", alimento.getNombre() + " " + alimento.getEstado());
			result.add(temp);
		}
		return result;
	}

	@Override
	public List<Alimento> ListarAlimentos() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'ListarAlimentos'");
	}

	@Override
	public void actualizar(Alimento alimento) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
	}
}
