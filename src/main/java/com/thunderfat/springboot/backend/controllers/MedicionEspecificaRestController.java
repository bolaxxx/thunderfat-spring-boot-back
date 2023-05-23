package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;
import com.thunderfat.springboot.backend.model.service.IMedicion_EspecificaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/medicion_especifica")
public class MedicionEspecificaRestController {
    private final IMedicion_EspecificaService medicionService;

    @Autowired
    public MedicionEspecificaRestController(IMedicion_EspecificaService medicionService) {
        this.medicionService = medicionService;
    }

    @GetMapping("/{id}")
    public List<MedicionEspecifica> listarPorPaciente(@PathVariable int id) {
        return medicionService.listarPorPaciente(id);
    }

    @GetMapping("/detalle/{id}")
    public MedicionEspecifica buscarPorId(@PathVariable int id) {
        return medicionService.buscarPorId(id);
    }

    @PostMapping("/save/{id}")
    public MedicionEspecifica anadirMedicion(@PathVariable("id") int idPaciente, @RequestBody MedicionEspecifica medicion) {
        medicionService.insertar(medicion, idPaciente);
        return medicion;
    }

    @PutMapping("/actualizar/{id}")
    public MedicionEspecifica actualizar(@PathVariable("id") int idPaciente, @RequestBody MedicionEspecifica medicion) {
        // Update logic goes here
        return null;
    }

    @DeleteMapping("/eliminar/{id}")
    public MedicionEspecifica borrar(@PathVariable("id") int idMedicion) {
        medicionService.eliminar(idMedicion);
        return null;
    }
}
