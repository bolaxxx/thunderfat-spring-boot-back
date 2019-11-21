package com.thunderfat.springboot.backend.controllers;

import java.awt.PageAttributes.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.service.IPacienteService;
@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping(value="/paciente")
public class PacienteRestController {
@Autowired
private IPacienteService servicepaciente;
@GetMapping(value="/todos/")
public List<Paciente> listarTodos(){
	return servicepaciente.ListarPaciente();
}
@GetMapping(value="/{id}/")
public List<Paciente> listarporNutriconista(@PathVariable(name="id") int id ) {
	return servicepaciente.listarPacienteNutrcionista(id);
}
@GetMapping(value="/detalle/{id}/")
public Paciente buscaPorId(@PathVariable(name="id") int id ) {
	
	return servicepaciente.buscarPorId(id);
}
@PostMapping(value="/save")
public Paciente guarPaciente(@RequestBody Paciente newPaciente) {
	System.out.println(newPaciente);
	servicepaciente.insertar(newPaciente);
	return null;
}
@DeleteMapping(value="/delete/{id}")
public void eliminarPorId(@PathVariable(name="id")int id) {
servicepaciente.eliminar(id);
}

@GetMapping(value="/searchFullName/")
public List<Paciente> buscarpor (@RequestParam("id")int id_nutricionista,@RequestParam("searchterm")String searchterm ){
return servicepaciente.buscarNombreCompleto(id_nutricionista, searchterm);
}
@GetMapping(value="/searchDni/")
public List<Paciente> buscarporDni (@RequestParam("id")int id_nutricionista,@RequestParam("searchterm")String searchterm ){
return servicepaciente.buscarPorDni(id_nutricionista, searchterm);
}
@GetMapping(value="/searchPhone/")
public List<Paciente> buscarporEmail (@RequestParam("id")int id_nutricionista,@RequestParam("searchterm")String searchterm ){
return servicepaciente.buscarPorTelefono( searchterm,id_nutricionista);
}
@DeleteMapping(value="/eliminar/{id}")

public ResponseEntity<?> borrarPaciente(@PathVariable("id")int id_paciente) {
	System.out.println("llego");
	Map<String,Object> response = new  HashMap<>(); 
	try {
		
	}catch (DataAccessException e) {
		// TODO: handle exception
		response.put("mensaje", "Error al actualizar el paciente en la base de datos");
		
		response.put("error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
		return new ResponseEntity<Map<String ,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	this.servicepaciente.eliminar(id_paciente);
	return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
}
@PutMapping(value="/actualizar/{id}")
//@ResponseStatus(HttpStatus.CREATED)
public Paciente  actualiar(@PathVariable("id")int id_paciente,@RequestBody Paciente paciente) {
	System.out.println("el paciente update"+paciente);
	Paciente pacienteactual = servicepaciente.buscarPorId(id_paciente);
	pacienteactual.setAltura(paciente.getAltura());
	pacienteactual.setApellidos(paciente.getApellidos());
	pacienteactual.setEmail(paciente.getEmail());
	pacienteactual.setCodigopostal(paciente.getCodigopostal());
	pacienteactual.setNombre(paciente.getNombre());	
	pacienteactual.setDireccion(paciente.getDireccion());
	pacienteactual.setDni(paciente.getDni());
	pacienteactual.setSexo(paciente.getSexo());
	pacienteactual.setLocalidad(paciente.getLocalidad());
	pacienteactual.setProvincia(paciente.getProvincia());
	pacienteactual.setFechanacimiento(paciente.getFechanacimiento());
	pacienteactual.setTelefono(paciente.getTelefono());
	this.servicepaciente.insertar(pacienteactual);
	return pacienteactual;
}
}

