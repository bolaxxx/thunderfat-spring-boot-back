package com.thunderfat.springboot.backend.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.entity.Ingrediente;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.service.IPlanDietaService;
@CrossOrigin(origins= {"http://localhost:4200","http://localhost:8100"})
@RestController
@RequestMapping(value="/plandieta")
public class PlanDietaRestController {
@Autowired
private IPlanDietaService servicefiltro;

@GetMapping(value="/{id}/")
public List<PlanDieta> listarporNutriconista(@PathVariable(name="id") int id ) {
	return servicefiltro.listarPorNutricionista(id);
}
@GetMapping(value="/detalle/{id}/")
public ResponseEntity<?> buscaPorId(@PathVariable(name="id") int id ) {
	System.out.println("llego al metodo\"");
	PlanDieta dieta =null;
	Map<String,Object> response= new HashMap();
	try {
		dieta=servicefiltro.buscarPorId(id);
	} catch (DataAccessException e) {
		// TODO: handle exception
		response.put("mensaje", "error al realizar la consulta");
		response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	if(dieta==null) {
	response.put("error: ","no hay plan de dieta con este id :"+id);
	return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
	} 
	return new ResponseEntity<PlanDieta>(dieta,HttpStatus.OK);
}
@PostMapping(value="/save/{nutricionista}/{paciente}")
public PlanDieta guarPaciente(@RequestBody PlanDieta newPaciente,
		@PathVariable("nutricionista")int id_nutricionista,@PathVariable("paciente")int id_paciente) {
	System.out.println(newPaciente);
	servicefiltro.insertar(newPaciente,id_nutricionista,id_paciente);
	return null;
}
@DeleteMapping(value="/delete/{id}")
public void eliminarPorId(@PathVariable(name="id")int id) {
servicefiltro.eliminar(id);
}

//@GetMapping(value="/searchFullName/")
//public List<Paciente> buscarpor (@RequestParam("id")int id_nutricionista,@RequestParam("searchterm")String searchterm ){
//return servicepaciente.buscarNombreCompleto(id_nutricionista, searchterm);
//}
//@GetMapping(value="/searchDni/")
//public List<Paciente> buscarporDni (@RequestParam("id")int id_nutricionista,@RequestParam("searchterm")String searchterm ){
//return servicepaciente.buscarPorDni(id_nutricionista, searchterm);
//}
//@GetMapping(value="/searchPhone/")
//public List<Paciente> buscarporEmail (@RequestParam("id")int id_nutricionista,@RequestParam("searchterm")String searchterm ){
//return servicepaciente.buscarPorTelefono( searchterm,id_nutricionista);
//}
//@DeleteMapping(value="/eliminar/{id}")
////@ResponseStatus(HttpStatus.NO_CONTENT)
//public void borrarPaciente(@PathVariable("id")int id_paciente) {
//	System.out.println("llego");
//	this.servicefiltro.eliminar(id_paciente);
//}
@PutMapping(value="/actualizar/{id}")
//@ResponseStatus(HttpStatus.CREATED)
public Paciente  actualiar(@PathVariable("id")int id_paciente,@RequestBody Paciente paciente) {
//	System.out.println("el paciente update"+paciente);
//	Paciente pacienteactual = servicepaciente.buscarPorId(id_paciente);
//	pacienteactual.setAltura(paciente.getAltura());
//	pacienteactual.setApellidos(paciente.getApellidos());
//	pacienteactual.setEmail(paciente.getEmail());
//	pacienteactual.setCodigopostal(paciente.getCodigopostal());
//	pacienteactual.setNombre(paciente.getNombre());	
//	pacienteactual.setDireccion(paciente.getDireccion());
//	pacienteactual.setDni(paciente.getDni());
//	pacienteactual.setSexo(paciente.getSexo());
//	pacienteactual.setLocalidad(paciente.getLocalidad());
//	pacienteactual.setProvincia(paciente.getProvincia());
//	pacienteactual.setFechanacimiento(paciente.getFechanacimiento());
//	pacienteactual.setTelefono(paciente.getTelefono());
//	this.servicepaciente.insertar(pacienteactual);
	return null;
}
@GetMapping(value="/listacompra/{id}/{start}")
public List<Ingrediente> listaCompra(@PathVariable("id")int id_paciente ,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable("start") LocalDate startDate ){
return servicefiltro.listadelacompra(id_paciente, startDate);
}
@GetMapping(value="/paciente/{id}/{start}")
public PlanDieta planpaciente(@PathVariable("id")int id_paciente ,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable("start") LocalDate startDate ){
return servicefiltro.buscarPlanActualPaciente(id_paciente, startDate);
}
@PostMapping(value="/update/{id_nutricionista}/{id_paciente}")
public PlanDieta updata(@PathVariable("id_nutricionista")int id_nutricionista,@PathVariable("id_paciente")int id_paciente,@RequestBody PlanDieta dieta) {
	this.servicefiltro.updatePlan(dieta, id_nutricionista, id_paciente);
	return null;
}
}
