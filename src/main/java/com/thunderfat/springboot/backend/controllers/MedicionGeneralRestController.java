package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.service.IMedicion_GeneralService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/medicion_general")
public class MedicionGeneralRestController {
    private final IMedicion_GeneralService medicionService;

    @Autowired
    public MedicionGeneralRestController(IMedicion_GeneralService medicionService) {
        this.medicionService = medicionService;
    }

    @GetMapping("/{id}")
    public List<MedicionGeneral> listarPorPaciente(@PathVariable int id) {
        return medicionService.listarPorPaciente(id);
    }

    @GetMapping("/detalle/{id}")
    public MedicionGeneral buscarPorId(@PathVariable int id) {
        return medicionService.buscarPorID(id);
    }

    @PostMapping("/save/{id}")
    public ResponseEntity<Map<String, Object>> anadirMedicion(@PathVariable("id") int id_paciente, @RequestBody MedicionGeneral medicion) {
        Map<String, Object> response = new HashMap<>();
        MedicionGeneral medicionresponse = null;
        try {
            medicionresponse = medicionService.insertar(medicion, id_paciente);
        } catch (DataAccessException e) {
            response.put("mensaje", "");
            response.put("error", e.getMessage().concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.put("mensaje", "La medicion general ha sido creada");
        response.put("medicion", medicionresponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/actualizar/{id}")
    public MedicionGeneral actualizar(@PathVariable("id") int id_paciente, @RequestBody MedicionGeneral medicion) {
        medicionService.update(medicion);
        return medicion;
    }

    @DeleteMapping("/eliminar/{id}")
    public void borrar(@PathVariable("id") int id_medicion) {
        medicionService.eliminar(id_medicion);
    }
}
