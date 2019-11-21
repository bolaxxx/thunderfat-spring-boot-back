package com.thunderfat.springboot.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;
import com.thunderfat.springboot.backend.model.service.IComidaService;

@CrossOrigin(origins={"http://localhost:4200","http://localhost:8100"})
@RestController
@RequestMapping(value="/comida")
public class ComidaRestController {
	@Autowired
	private IComidaService serviceCita;
	

	@GetMapping(value="/detalle/{id}")
	public Comida detalle(@PathVariable("id") int id_cita) {
		
		return serviceCita.buscarPorID(id_cita);
	}
	@PostMapping(value="/save/{id}")
	public Comida guardar(@PathVariable ("id") int id_nutricionista, @RequestBody Comida cita) {
		serviceCita.insertar(cita);
		return null;
	}

	@DeleteMapping(value="/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable("id") int  id_cita) {
		this.serviceCita.eliminar(id_cita);
	}
	
	@GetMapping(value="/cambios/{id_plato}/{id_paciente")
	public List<PlatoPredeterminado>buscarPosiblesCambios(@PathVariable("id_plato")int id_plato,@PathVariable("id_paciente")int id_paciente){
	 Map<String,Object> response = new HashMap();
	 
		return this.serviceCita.bucarcambios(id_paciente,id_plato);
	}
}
