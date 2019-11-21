package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.NutricionistaRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Service
public class PacienteServiceJPA implements IPacienteService{
	@Autowired
	private PacienteRepository repo;
	@Autowired
	private NutricionistaRepository nutrirepo;
@Autowired
private UserServiceJPA userservice;
	public List<Paciente> ListarPaciente() {
		List <Paciente>pacientes=repo.findAll();
		System.out.println(pacientes.toString());
		return pacientes;
	}

	public void insertar(Paciente paciente) {
		repo.save(paciente);// TODO Auto-generated method stub
		
	}

	public Paciente buscarPorId(int id_paciente) {
		Optional<Paciente> optional = repo.findById(id_paciente);
		if (optional.isPresent())
			return optional.get();
		return null;
	
	}
	@Transactional
	public void eliminar(int id_paciente) {
		Paciente pac = this.repo.findById(id_paciente).get();
		Nutricionista nutricionista = this.nutrirepo.findById(pac.getNutricionista().getId()).get();
		pac.getRoles().clear();
		pac.getCitas().clear();
		pac.getPlanesdieta().clear();
		pac.getAntecedentesclinicos().clear();
		pac.getAntecedentestratamientos().clear();
		//nutricionista.getPacientes().remove(pac);
		//pac.setNutricionista(null);
		//this.repo.save(pac);
			nutricionista.removePaciente(pac);
		//this.userservice.deletebyid(id_paciente);
		this.nutrirepo.save(nutricionista);
	}

	
//
//	@Override
//	public List<Paciente> listarPacienteNutrcionista(int id_nutricionista) {
//		Optional <Nutricionista>op=nutrirepo.findById(id_nutricionista);
//			if(op.isPresent()) {
//				Nutricionista nutri =op.get();
//				//System.out.println(nutri);
//				List <Paciente> pacientes= repo.findByNutricionista(nutri);
//				//System.out.println(pacientes);
//				return pacientes;
//				}
//		return null;
//	}
//
//	@Override
//	public ArrayList<Map> listarPacienteNutricionistaSelect(int id_nutricionista) {
//		// TODO Auto-generated method stub
//		List<Paciente>lista=repo.buscarporNutricionista(nutrirepo.findById(id_nutricionista).get());
//		ArrayList<Map> result = new ArrayList();
//		for (int i = 0; i < lista.size(); i++) {
//			/*
//			 * "id": 293, "title": "Event 1", "url": "http://example.com", "class":
//			 * "event-important", "start": 12039485678000, // Milliseconds "end":
//			 * 1234576967000 // Milliseconds
//			 */
//			Map<String, Object> temp = new LinkedHashMap<String, Object>();
//			temp.put("id", lista.get(i).getId());
//			//temp.put("title", citas.get(i).getPaciente().getNombre() + ' ' + citas.get(i).getPaciente().getApellidos());
//			//temp.put("url", "/thunderfat/alimento/index");
//			// temp.put("class", "event-important");
//			//temp.put("start", citas.get(i).getFecha_ini().format(DateTimeFormatter.ISO_DATE_TIME));
//			temp.put("text",lista.get(i).getNombre()+" "+lista.get(i).getApellidos()+" "+lista.get(i).getDni());
//			result.add(temp);
//		}
//		// respuesta.put("success", 1);
//		// respuesta.put("result",result);
//		System.out.println(result);
//		return result;
//		
//	}
	@Transactional(readOnly=true)
	@Override
	public List<Paciente> listarPacienteNutrcionista(int id_nutricionista) {
	 // TODO Auto-generated method stub 
		
		//List<Paciente>list = repo.findNutricionistaById(id_nutricionista);
		//System.out.println(list.toString()+"liusta recien sacada del repo");
		return repo.buscarPorNutricionista(id_nutricionista) ;
	}

	@Override
	public List<Paciente>buscarNombreCompleto (int id ,String searchterm ) {
		
			return repo.findBySearchString(searchterm,id);
	}

	@Override
	public List<Paciente> buscarPorDni(int id, String Dni) {
		// TODO Auto-generated method stub
		return repo.buscardni(Dni, id);
	}

	@Override
	public List<Paciente> buscarPorTelefono(String email, int id) {
		// TODO Auto-generated method stub
		return repo.buscarporEmail(email, id);
	}

	







	
 

}
