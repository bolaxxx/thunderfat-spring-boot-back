package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;
import com.thunderfat.springboot.backend.model.service.IComidaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/comida")
public class ComidaRestController {
    @Autowired
	private final IComidaService serviceCita;

    
    public ComidaRestController(IComidaService serviceCita) {
        this.serviceCita = serviceCita;
    }

    @GetMapping("/detalle/{id}")
    public Comida detalle(@PathVariable("id") int idCita) {
        return serviceCita.buscarPorID(idCita);
    }

    @PostMapping("/save/{id}")
    public Comida guardar(@PathVariable("id") int idNutricionista, @RequestBody Comida comida) {
        serviceCita.insertar(comida);
        return comida;
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") int idCita) {
        serviceCita.eliminar(idCita);
    }

    @GetMapping("/cambios/{id_plato}/{id_paciente}")
    public List<PlatoPredeterminado> buscarPosiblesCambios(@PathVariable("id_plato") int idPlato,
                                                           @PathVariable("id_paciente") int idPaciente) {
        Map<String, Object> response = new HashMap<>();
        return serviceCita.bucarcambios(idPaciente, idPlato);
    }
}
