package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.CitaRepository;
import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Service
public class CitaServiceJPA implements ICitaService {
	@Autowired
	private CitaRepository citarepo;
	@Autowired
	private PacienteServiceJPA pacienteService;
	@Autowired 
	private NutricionistaServiceJPA nutricionistaService;

	public List<Cita> ListarCita() {
		List <Cita> citas=citarepo.findAll();
		// TODO Auto-generated method stub
		return citas;
	}

	@Override
	public List<Cita> ListarPorPaciente(Paciente paciente) {
//		citarepo.findByPaciente(paciente);
//		// TODO Auto-generated method stub
//		return citarepo.findByPaciente(paciente);
		return null;
	}

	

	@Override
	@Transactional
	public void insertar(Cita cita,int id_paciente) {
		System.out.println(cita.getPaciente()+" cita desde guardar ");
		cita.setNutricionista(this.nutricionistaService.buscarPorId(id_paciente));
		cita.getNutricionista().getCitas().add(cita);
		cita.getPaciente().setNutricionista(cita.getNutricionista());
		cita.getPaciente().getCitas().add(cita);
		//citarepo.save(cita);
		this.nutricionistaService.guardar(cita.getNutricionista());
		//this.pacienteService.insertar(cita.getPaciente());
		
	}

	

	@Override
	@Transactional(readOnly=true)
	public Cita buscarPorID(int id_cita) {
	
		Optional<Cita>op=citarepo.findById(id_cita);
		if(op.isPresent()) {
			//System.out.println(op.get().getNutricionista().getId());
			//op.get().getPaciente();
			Cita cita= new Cita();
			System.out.println(op.get().toString()+"esto e slo que hay ");
			
			//System.out.println(cita);
			return op.get();}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public void eliminar(int id_cita) {
		// TODO Auto-generated method stub
		Cita cita = citarepo.findById(id_cita).get();
		cita.setPaciente(null);
		cita.setNutricionista(null);
		citarepo.deleteById(id_cita);
	}

	@Override
	@Transactional(readOnly=true)
	public ArrayList<Map> ListarPorPacienteEntreFechas(int nutricionista, LocalDate start, LocalDate end) {
		// TODO Auto-generated method stub
		List<Cita>citas=citarepo.encontrarCitasNutricionistaFechas(nutricionista, start, end);
		ArrayList<Map> result = new ArrayList();
		for (int i = 0; i < citas.size(); i++) {
			//System.out.println(citas.get(i).toString());
			/*
			 * "id": 293, "title": "Event 1", "url": "http://example.com", "class":
			 * "event-important", "start": 12039485678000, // Milliseconds "end":
			 * 1234576967000 // Milliseconds
			 */
			Map<String, Object> temp = new LinkedHashMap<String, Object>();
			temp.put("id", citas.get(i).getId ());
			
			temp.put("title", citas.get(i).getPaciente().getNombre() + ' ' + citas.get(i).getPaciente().getApellidos());
			//temp.put("url", "/thunderfat/alimento/index");
			// temp.put("class", "event-important");
			temp.put("start", citas.get(i).getFechaini().format(DateTimeFormatter.ISO_DATE_TIME));
			temp.put("end", citas.get(i).getFechafin().format(DateTimeFormatter.ISO_DATE_TIME));
			result.add(temp);
		}
		// respuesta.put("success", 1);
		// respuesta.put("result",result);
		System.out.println(result);
		return result;
		//return lista;
	}

	@Override
	@Transactional(readOnly=true)
	public Cita buscarproximacita(int id_paciente, LocalDate start) {
		// TODO Auto-generated method stub
		return citarepo.proximacita(id_paciente, start);
	}

}
