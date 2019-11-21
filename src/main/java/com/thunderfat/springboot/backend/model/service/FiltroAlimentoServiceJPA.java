package com.thunderfat.springboot.backend.model.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.FiltroAlimentarioRepository;
import com.thunderfat.springboot.backend.model.entity.Alimento;
import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;

@Service
public class FiltroAlimentoServiceJPA implements IFiltroAlimentarioService{
	@Autowired 
	private FiltroAlimentarioRepository filtrorepo;
	@Autowired
	private AlimentoServiceJPA alimentoService;
	@Autowired
	private NutricionistaServiceJPA serviceNutricionista;

//	@Override
//	public List<FiltroAlimentario> listarporNutricionista(Nutricionista nutricionista) {
//		
//		// TODO Auto-generated method stub
//		return filtrorepo.findByNutricionista(nutricionista);
//	}

	@Override
	@Transactional(readOnly=true)
	public FiltroAlimentario buscarPorId(int id_filtroalimentario) {
		Optional<FiltroAlimentario>op= filtrorepo.findById(id_filtroalimentario);
		if(op.isPresent())
			return op.get();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional	
	public void insertar(FiltroAlimentario filtroalimentario, int id_nutricionista) {
		// TODO Auto-generated method stub
		System.out.println(filtroalimentario);
		Nutricionista nutri = serviceNutricionista.buscarPorId(id_nutricionista);
		System.out.println(nutri);
//		filtrorepo.save(filtroalimentario);
		nutri.getFiltros().add(filtroalimentario);
		serviceNutricionista.guardar(nutri);
	}

	@Override
	public void eliminar(int id_filtroalimentario) {
		// TODO Auto-generated method stub
		filtrorepo.deleteById(id_filtroalimentario);
		
	}

	@Override
	public List<FiltroAlimentario> listarTodos() {
		// TODO Auto-generated method stub
		return filtrorepo.findAll();
	}

	@Override
	public ArrayList<Map> alimentosEnFiltro(int id_filtro) {
		FiltroAlimentario filtro =buscarPorId(id_filtro);
		ArrayList<Map> result = new ArrayList(); 
		for(int i =0;i<filtro.getAlimentos().size();i++) {
			Map<String, Object> temp = new LinkedHashMap<String, Object>();
			temp.put("id", filtro.getAlimentos().get(i).getId());
			temp.put("text", filtro.getAlimentos().get(i).getNombre());
			result.add(temp);
		 }
		// TODO Auto-generated method stub
		return result;
	}

	@Override
	public int InsetarAjax(Map filtrojson) {

		System.out.println(filtrojson);
		String nombre = (String) filtrojson.get("nombre");
		System.out.println(filtrojson.get("alimentos"));
		String alimentoString=filtrojson.get("alimentos").toString();
		System.out.println(alimentoString);
		String[] items = alimentoString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").replaceAll(" ", "").split(",");

		int[] results = new int[items.length];

		for (int i = 0; i < items.length; i++) {
		    try {
		        results[i] = Integer.parseInt(items[i]);
		    } catch (NumberFormatException nfe) {
		        //NOTE: write something here if you need to recover from formatting errors
		    };
		}
		ArrayList<Alimento> listasAlimentos = new ArrayList <Alimento>();
		for (int j=0;j<results.length;j++) {
			System.out.println(results[j]+"resultado en int ");
			listasAlimentos.add(alimentoService.buscarPorId(results[j]));
		}
		FiltroAlimentario filtroAlimentario = new FiltroAlimentario();
		filtroAlimentario.setAlimentos(listasAlimentos);
		filtroAlimentario.setNombre(nombre);
		//filtroAlimentario.setNutricionista(serviceNutricionista.buscarPorId((int)filtrojson.get("id_nutricionista")));
		if(filtrojson.containsKey("id_filtro")==true) {
			filtroAlimentario.setId((int)filtrojson.get("id_filtro"));
		}
		filtrorepo.save(filtroAlimentario);
		return filtroAlimentario.getId();
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<FiltroAlimentario> listarporNutricionista(int id_nutricionista) {
		// TODO Auto-generated method stub
		return filtrorepo.buscarpornutricionista(id_nutricionista);
	}

}
