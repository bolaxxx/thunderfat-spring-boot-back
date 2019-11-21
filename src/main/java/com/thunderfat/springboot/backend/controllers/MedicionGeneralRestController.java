package com.thunderfat.springboot.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.service.IMedicion_GeneralService;

@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping(value="/medicion_general")
public class MedicionGeneralRestController {
	@Autowired
	private IMedicion_GeneralService medicionService;
	
	@GetMapping(value="/{id}/")
	public List<MedicionGeneral> listarPorPaciente(@PathVariable(name="id")int id){
		
		return this.medicionService.listarPorPaciente(id);
	}
	@GetMapping(value="/detalle/{id}")
	public MedicionGeneral buscarporId(@PathVariable(name="id")int id ) {
		return this.medicionService.buscarPorID(id);
	}
	@PostMapping(value="/save/{id}")
	public ResponseEntity<?> anadirMedicion(@PathVariable("id") int id_paciente,@RequestBody MedicionGeneral medicion){
		Map<String,Object>response=new HashMap<>();
		MedicionGeneral medicionresponse=null;
		try {
			medicionresponse=this.medicionService.insertar(medicion, id_paciente);
		}catch (DataAccessException e) {
			// TODO: handle exception
			response.put("mensaje", "");
			response.put("error", e.getMessage().concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "La medicion general ha sido creada ");
		response.put("medicion", medicionresponse);
		return  new ResponseEntity<Map<String, Object>>(response,HttpStatus.CREATED);
	}
	@PutMapping(value="actualizar/{id}")
	public MedicionGeneral actulizar(@PathVariable("id")int id_paciente,@RequestBody MedicionGeneral medicion) {
		this.medicionService.update(medicion);
		return medicion;
	}
	
	@DeleteMapping(value="eliminar/{id}")
	public MedicionGeneral borrar(@PathVariable("id")int id_medicion) {
		this.medicionService.eliminar(id_medicion);
		return null;
	}
}
