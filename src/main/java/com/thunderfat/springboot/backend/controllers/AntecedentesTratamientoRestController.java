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

import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;
import com.thunderfat.springboot.backend.model.service.IAntecedente_TratamientoService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping(value = "/antecedente_tratamiento")
public class AntecedentesTratamientoRestController {
	@Autowired
	private IAntecedente_TratamientoService antecedenteTratamientoService;
	
	@GetMapping(value = "/{id}/")
	public List<AntecedenteTratamiento> listarPorPaciente(@PathVariable int id) {
		return antecedenteTratamientoService.buscarPorPaciente(id);
	}
	
	@GetMapping(value = "/detalle/{id}")
	public AntecedenteTratamiento buscarPorId(@PathVariable int id) {
		return antecedenteTratamientoService.buscarPorId(id);
	}
	
	@PostMapping(value = "/save/{id}")
	public AntecedenteTratamiento anadirMedicion(@PathVariable("id") int id_paciente, @RequestBody AntecedenteTratamiento medicion) {
		antecedenteTratamientoService.insertar(medicion, id_paciente);
		return medicion;
	}
	
	@PutMapping(value = "/actualizar/{id}")
	public AntecedenteTratamiento actualizar(@PathVariable("id") int id_paciente, @RequestBody AntecedenteTratamiento medicion) {
		// Implement update logic here
		return medicion;
	}
	
	@DeleteMapping(value = "/eliminar/{id}")
	public void borrar(@PathVariable("id") int id_medicion) {
		antecedenteTratamientoService.eliminar(id_medicion);
	}
}
