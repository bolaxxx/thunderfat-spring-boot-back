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

import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;
import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;
import com.thunderfat.springboot.backend.model.service.IAntecedente_ClinicoService;

@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping(value="/antecedente_clinico")
public class AntecedentesClinicosRestController {
	@Autowired
	private IAntecedente_ClinicoService medicionService;
	
	@GetMapping(value="/{id}/")
	public List<AntecedentesClinicos> listarPorPaciente(@PathVariable("id")int id){
		
		return this.medicionService.listarAntecedenteporPaciente(id);
	}
	@GetMapping(value="/detalle/{id}")
	public AntecedentesClinicos buscarporId(@PathVariable(name="id")int id ) {
		return this.medicionService.buscarPorID(id);
	}
	@PostMapping(value="/save/{id}")
	public AntecedentesClinicos anadirMedicion(@PathVariable("id") int id_paciente,@RequestBody AntecedentesClinicos medicion){
		
	this.medicionService.insertar(medicion, id_paciente);
		return null;
	}
	@PutMapping(value="actualizar/{id}")
	public AntecedentesClinicos actulizar(@PathVariable("id")int id_paciente,@RequestBody AntecedentesClinicos medicion) {
		
		return null;
	}
	
	@DeleteMapping(value="eliminar/{id}")
	public MedicionSegmental borrar(@PathVariable("id")int id_medicion) {
		this.medicionService.eliminar(id_medicion);
		return null;
	}
}
