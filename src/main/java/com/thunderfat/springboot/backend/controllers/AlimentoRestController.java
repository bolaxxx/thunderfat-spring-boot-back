package com.thunderfat.springboot.backend.controllers;

import java.util.List;

import javax.validation.Valid;

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

import com.thunderfat.springboot.backend.model.entity.Alimento;
import com.thunderfat.springboot.backend.model.service.IAlimentoService;
@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping(value = "/alimentos")
public class AlimentoRestController {
	@Autowired
	private IAlimentoService alimentoservice;

	@GetMapping(value = "/")
	public List<Alimento> index() {
		return alimentoservice.ListarAlimentos();
	};

	@GetMapping(value = "/{id}")
	public Alimento get(@PathVariable(name = "id") int id) {
		return alimentoservice.buscarPorId(id);

	}
	@PostMapping(value="/save")
	public Alimento saveAlimento(@Valid Alimento alimento) {
		this.alimentoservice.insertar(alimento);
		return alimento;
	
	}
	@PutMapping(value="/update")
	public void updateAlimento(@RequestBody Alimento alimento) {
		
	}
	@DeleteMapping(value="/delete/{id}")
	public void borrarAlimentos( @PathVariable(name="id")int id ) {
		this.alimentoservice.eliminar(id);
	}
}
