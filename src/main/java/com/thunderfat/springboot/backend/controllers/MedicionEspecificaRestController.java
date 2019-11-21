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

import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;
import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.service.IMedicion_EspecificaService;
import com.thunderfat.springboot.backend.model.service.IMedicion_GeneralService;

@CrossOrigin(origins={"http://localhost:4200","http://localhost:8100"})
@RestController
@RequestMapping(value="/medicion_especifica")
public class MedicionEspecificaRestController {
	@Autowired
	private IMedicion_EspecificaService medicionService;
	
	@GetMapping(value="/{id}/")
	public List<MedicionEspecifica> listarPorPaciente(@PathVariable(name="id")int id){
		
		return this.medicionService.listarPorPaciente(id);
	}
	@GetMapping(value="/detalle/{id}")
	public MedicionEspecifica buscarporId(@PathVariable(name="id")int id ) {
		return this.medicionService.buscarPorId(id);
	}
	@PostMapping(value="/save/{id}")
	public MedicionEspecifica anadirMedicion(@PathVariable("id") int id_paciente,@RequestBody MedicionEspecifica medicion){
		
	this.medicionService.insertar(medicion, id_paciente);
		return null;
	}
	@PutMapping(value="actualizar/{id}")
	public MedicionEspecifica actulizar(@PathVariable("id")int id_paciente,@RequestBody MedicionEspecifica medicion) {
		
		return null;
	}
	
	@DeleteMapping(value="eliminar/{id}")
	public MedicionEspecifica borrar(@PathVariable("id")int id_medicion) {
		this.medicionService.eliminar(id_medicion);
		return null;
	}
}
