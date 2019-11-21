package com.thunderfat.springboot.backend.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.model.service.ICitaService;

@CrossOrigin(origins={"http://localhost:4200","http://localhost:8100"})
@RestController
@RequestMapping(value="/cita")
public class CitaRestController {
	@Autowired
	private ICitaService serviceCita;
	
	@GetMapping(value="/nutricionista/{id}")
	public List<Cita> listarPorNutricionista(@PathVariable(value="{id}") int id ){
	
		return null;
	}
	@GetMapping(value = "/citasNutricionista/{id}")
	ArrayList<Map> getCitasPaciente(@PathVariable("id") int id_nutricionista,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("start") LocalDate startDate,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("end") LocalDate endDate) {

		// Map<String, Object> respuesta = new LinkedHashMap<String, Object>();
		//Nutricionista nutricionista = serviceNutricionista.buscarPorId(id_nutricionista);
		ArrayList<Map> result = serviceCita.ListarPorPacienteEntreFechas(id_nutricionista, startDate, endDate);

		return result;
	}
	@GetMapping(value="/detalle/{id}")
	public Cita detalle(@PathVariable("id") int id_cita) {
		
		return serviceCita.buscarPorID(id_cita);
	}
	@PostMapping(value="/save/{id}")
	public Cita guardar(@PathVariable ("id") int id_nutricionista, @RequestBody Cita cita) {
		serviceCita.insertar(cita,id_nutricionista);
		return null;
	}

	@DeleteMapping(value="/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable("id") int  id_cita) {
		this.serviceCita.eliminar(id_cita);
	}
	@GetMapping(value="/nextcita/{id}/{start}")
	public Cita proximacita(@PathVariable("id")int id_paciente,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable("start") LocalDate startDate) {
			return serviceCita.buscarproximacita(id_paciente, startDate);
	}
}
