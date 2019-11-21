package com.thunderfat.springboot.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;
import com.thunderfat.springboot.backend.model.service.IFiltroAlimentarioService;
import com.thunderfat.springboot.backend.model.service.IPlatoPredetereminadoService;
@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping(value="/platopredeterminado")
public class PlatoPredeterminadoRestController {
@Autowired
private IPlatoPredetereminadoService servicefiltro;

@GetMapping(value="/{id}/")
public List<PlatoPredeterminado> listarporNutriconista(@PathVariable(name="id") int id ) {
	return servicefiltro.listarPorNutricionista(id);
}
@GetMapping(value="/detalle/{id}/")
public PlatoPredeterminado buscaPorId(@PathVariable(name="id") int id ) {
	
	return servicefiltro.buscarPorId(id);
}
@PostMapping(value="/save/{id}")
public PlatoPredeterminado guarPaciente(@RequestBody PlatoPredeterminado newPaciente,@PathVariable("id")int id_nutricionista) {
	System.out.println(newPaciente);
	
	return servicefiltro.insertar(newPaciente,id_nutricionista);
}
@DeleteMapping(value="/delete/{id}")
public void eliminarPorId(@PathVariable(name="id")int id) {
servicefiltro.eliminarPlatoPredeterminado(id);
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
}

