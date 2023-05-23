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

import com.thunderfat.springboot.backend.model.entity.Alimento;
import com.thunderfat.springboot.backend.model.service.IAlimentoService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/alimentos")
public class AlimentoRestController {
	@Autowired
	private IAlimentoService alimentoservice;

	@GetMapping("/")
	public List<Alimento> listarAlimentos() {
		return alimentoservice.ListarAlimentos();
	}

	@GetMapping("/{id}")
	public Alimento obtenerAlimento(@PathVariable int id) {
		return alimentoservice.buscarPorId(id);
	}

	@PostMapping("/save")
	public Alimento guardarAlimento(@RequestBody Alimento alimento) {
		alimentoservice.insertar(alimento);
		return alimento;
	}

	@PutMapping("/update")
	public void actualizarAlimento(@RequestBody Alimento alimento) {
		alimentoservice.actualizar(alimento);
	}

	@DeleteMapping("/delete/{id}")
	public void borrarAlimento(@PathVariable int id) {
		alimentoservice.eliminar(id);
	}
}
