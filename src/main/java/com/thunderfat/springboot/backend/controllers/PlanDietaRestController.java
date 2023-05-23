package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.entity.Ingrediente;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.service.IPlanDietaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/plandieta")
public class PlanDietaRestController {
    private final IPlanDietaService planDietaService;

    @Autowired
    public PlanDietaRestController(IPlanDietaService planDietaService) {
        this.planDietaService = planDietaService;
    }

    @GetMapping("/{id}")
    public List<PlanDieta> listarPorNutricionista(@PathVariable int id) {
        return planDietaService.listarPorNutricionista(id);
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        PlanDieta dieta;
        Map<String, Object> response = new HashMap<>();
        try {
            dieta = planDietaService.buscarPorId(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (dieta == null) {
            response.put("error", "No se encontró ningún plan de dieta con el ID: " + id);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dieta, HttpStatus.OK);
    }

    @PostMapping("/save/{nutricionista}/{paciente}")
    public ResponseEntity<Void> guardarPaciente(@RequestBody PlanDieta newPaciente,
                                                @PathVariable("nutricionista") int idNutricionista,
                                                @PathVariable("paciente") int idPaciente) {
        planDietaService.insertar(newPaciente, idNutricionista, idPaciente);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable int id) {
        planDietaService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable("id") int idPaciente, @RequestBody Paciente paciente) {
        // Update logic goes here
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/listacompra/{id}/{start}")
    public List<Ingrediente> listaCompra(@PathVariable("id") int idPaciente,
                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable("start") LocalDate startDate) {
        return planDietaService.listadelacompra(idPaciente, startDate);
    }

    @GetMapping("/paciente/{id}/{start}")
    public PlanDieta planPaciente(@PathVariable("id") int idPaciente,
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @PathVariable("start") LocalDate startDate) {
        return planDietaService.buscarPlanActualPaciente(idPaciente, startDate);
    }

    @PostMapping("/update/{id_nutricionista}/{id_paciente}")
    public ResponseEntity<Void> update(@PathVariable int idNutricionista, @PathVariable int idPaciente, @RequestBody PlanDieta dieta) {
        planDietaService.updatePlan(dieta, idNutricionista, idPaciente);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
