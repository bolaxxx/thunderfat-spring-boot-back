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

import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;
import com.thunderfat.springboot.backend.model.service.IMedicion_SegmentalService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/medicion_segmental")
public class MedicionSegmentalRestController {
    /**
     *
     */
    @Autowired
	private final IMedicion_SegmentalService medicionService;

    
    public MedicionSegmentalRestController(IMedicion_SegmentalService medicionService) {
        this.medicionService = medicionService;
    }

    @GetMapping("/{id}")
    public List<MedicionSegmental> listarPorPaciente(@PathVariable int id) {
        return medicionService.listarPorPaciente(id);
    }

    @GetMapping("/detalle/{id}")
    public MedicionSegmental buscarPorId(@PathVariable int id) {
        return medicionService.buscarPorID(id);
    }

    @PostMapping("/save/{id}")
    public void anadirMedicion(@PathVariable("id") int id_paciente, @RequestBody MedicionSegmental medicion) {
        medicionService.insertar(medicion, id_paciente);
    }

    @PutMapping("/actualizar/{id}")
    public void actualizar(@PathVariable("id") int id_paciente, @RequestBody MedicionSegmental medicion) {
        medicionService.actualizar(medicion, id_paciente);
    }

    @DeleteMapping("/eliminar/{id}")
    public void eliminar(@PathVariable("id") int id_medicion) {
        medicionService.eliminar(id_medicion);
    }
}
