package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;
import com.thunderfat.springboot.backend.model.service.IFiltroAlimentarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/filtroAlimentario")
public class FiltroAlimentarioRestController {
    @Autowired
	private final IFiltroAlimentarioService servicefiltro;

    
    public FiltroAlimentarioRestController(IFiltroAlimentarioService servicefiltro) {
        this.servicefiltro = servicefiltro;
    }

    @GetMapping("/{id}")
    public List<FiltroAlimentario> listarPorNutricionista(@PathVariable int id) {
        return servicefiltro.listarporNutricionista(id);
    }

    @GetMapping("/detalle/{id}")
    public FiltroAlimentario buscarPorId(@PathVariable int id) {
        return servicefiltro.buscarPorId(id);
    }

    @PostMapping("/save/{id}")
    public FiltroAlimentario guardarFiltroAlimentario(@RequestBody FiltroAlimentario newFiltro,
                                                      @PathVariable("id") int idNutricionista) {
        servicefiltro.insertar(newFiltro, idNutricionista);
        return newFiltro;
    }

    @DeleteMapping("/delete/{id}")
    public void eliminarPorId(@PathVariable int id) {
        servicefiltro.eliminar(id);
    }

    @DeleteMapping("/eliminar/{id}")
    public void borrarFiltroAlimentario(@PathVariable("id") int idFiltro) {
        servicefiltro.eliminar(idFiltro);
    }

    @PutMapping("/actualizar/{id}")
    public FiltroAlimentario actualizarFiltroAlimentario(@PathVariable("id") int idFiltro,
                                                         @RequestBody FiltroAlimentario filtro) {
        // Update logic goes here
        return null;
    }
}
