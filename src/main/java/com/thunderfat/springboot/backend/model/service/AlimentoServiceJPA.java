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
	@Transactional(readOnly=true)
	public List<Alimento> ListarAlimentos() {
		// TODO Auto-generated method stub
		List<Alimento>alimentos=alimentorepo.findAll();
		return alimentos;
	}
	@Transactional
	public void insertar(Alimento alimento) {
		// TODO Auto-generated method stub
		alimentorepo.save(alimento);
		
	}
	@Transactional(readOnly=true)
	public Alimento buscarPorId(int id_alimento) {

		Optional<Alimento> optional = alimentorepo.findById(id_alimento);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}
	@Transactional
	public void eliminar(int id_alimento) {
		alimentorepo.deleteById(id_alimento);
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Map> listarParaSelect() {
		List<Alimento>alimentos =alimentorepo.findAll();
		// TODO Auto-generated method stub
		ArrayList<Map> result = new ArrayList();
		for (int i = 0; i < alimentos.size(); i++) {
			/*
			 * "id": 293, "title": "Event 1", "url": "http://example.com", "class":
			 * "event-important", "start": 12039485678000, // Milliseconds "end":
			 * 1234576967000 // Milliseconds
			 */
			Map<String, Object> temp = new LinkedHashMap<String, Object>();
			temp.put("id", alimentos.get(i).getId());
			temp.put("text", alimentos.get(i).getNombre()+ ' ' + alimentos.get(i).getEstado());
			//temp.put("url", "/thunderfat/alimento/index");
			// temp.put("class", "event-important");
						result.add(temp);
		}
			System.out.println(result);
		return result;
	}



}
