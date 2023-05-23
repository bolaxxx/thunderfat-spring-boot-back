package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.service.IPacienteService;

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
@RequestMapping("/paciente")
public class PacienteRestController {
    private final IPacienteService pacienteService;

    @Autowired
    public PacienteRestController(IPacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping("/todos")
    public List<Paciente> listarTodos() {
        return pacienteService.ListarPaciente();
    }

    @GetMapping("/{id}")
    public List<Paciente> listarPorNutricionista(@PathVariable int id) {
        return pacienteService.listarPacienteNutrcionista(id);
    }

    @GetMapping("/detalle/{id}")
    public Paciente buscarPorId(@PathVariable int id) {
        return pacienteService.buscarPorId(id);
    }

    @PostMapping("/save")
    public ResponseEntity<Void> guardarPaciente(@RequestBody Paciente newPaciente) {
        pacienteService.insertar(newPaciente);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable int id) {
        pacienteService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/searchFullName")
    public List<Paciente> buscarPorNombreCompleto(@RequestParam("id") int idNutricionista, @RequestParam("searchterm") String searchTerm) {
        return pacienteService.buscarNombreCompleto(idNutricionista, searchTerm);
    }

    @GetMapping("/searchDni")
    public List<Paciente> buscarPorDni(@RequestParam("id") int idNutricionista, @RequestParam("searchterm") String searchTerm) {
        return pacienteService.buscarPorDni(idNutricionista, searchTerm);
    }

    @GetMapping("/searchPhone")
    public List<Paciente> buscarPorTelefono(@RequestParam("id") int idNutricionista, @RequestParam("searchterm") String searchTerm) {
        return pacienteService.buscarPorTelefono(searchTerm, idNutricionista);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Map<String, Object>> borrarPaciente(@PathVariable("id") int idPaciente) {
        Map<String, Object> response = new HashMap<>();
        try {
            pacienteService.eliminar(idPaciente);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el paciente en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Paciente> actualizar(@PathVariable("id") int idPaciente, @RequestBody Paciente paciente) {
        Paciente pacienteActual = pacienteService.buscarPorId(idPaciente);
        if (pacienteActual == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        pacienteActual.setAltura(paciente.getAltura());
        pacienteActual.setApellidos(paciente.getApellidos());
        pacienteActual.setEmail(paciente.getEmail());
        pacienteActual.setCodigopostal(paciente.getCodigopostal());
        pacienteActual.setNombre(paciente.getNombre());
        pacienteActual.setDireccion(paciente.getDireccion());
        pacienteActual.setDni(paciente.getDni());
        pacienteActual.setSexo(paciente.getSexo());
        pacienteActual.setLocalidad(paciente.getLocalidad());
        pacienteActual.setProvincia(paciente.getProvincia());
        pacienteActual.setFechanacimiento(paciente.getFechanacimiento());
        pacienteActual.setTelefono(paciente.getTelefono());
        pacienteService.insertar(pacienteActual);
        return new ResponseEntity<>(pacienteActual, HttpStatus.OK);
    }
}
