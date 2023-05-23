package com.thunderfat.springboot.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;
import com.thunderfat.springboot.backend.model.service.IPlatoPredetereminadoService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping(value = "/platopredeterminado")
public class PlatoPredeterminadoRestController {
    @Autowired
    private IPlatoPredetereminadoService servicefiltro;

    @GetMapping(value = "/{id}/")
    public List<PlatoPredeterminado> listarporNutriconista(@PathVariable int id) {
        return servicefiltro.listarPorNutricionista(id);
    }

    @GetMapping(value = "/detalle/{id}/")
    public PlatoPredeterminado buscaPorId(@PathVariable int id) {
        return servicefiltro.buscarPorId(id);
    }

    @PostMapping(value = "/save/{id}")
    public PlatoPredeterminado guarPaciente(@RequestBody PlatoPredeterminado newPaciente, @PathVariable("id") int id_nutricionista) {
        return servicefiltro.insertar(newPaciente, id_nutricionista);
    }

    @DeleteMapping(value = "/delete/{id}")
    public void eliminarPorId(@PathVariable int id) {
        servicefiltro.eliminarPlatoPredeterminado(id);
    }

    @PutMapping(value = "/actualizar/{id}")
    public void actualiar(@PathVariable("id") int id_paciente, @RequestBody PlatoPredeterminado paciente) {
        // Update logic goes here
    }
}
