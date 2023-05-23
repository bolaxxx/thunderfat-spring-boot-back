package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.PlanDietaRepository;
import com.thunderfat.springboot.backend.model.entity.Ingrediente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;

@Service
public class PlanDietaServiceJPA implements IPlanDietaService {
	@Autowired
	private PlanDietaRepository repo;
	@Autowired
	private NutricionistaServiceJPA serviceNutricionsta;
	@Autowired
	private PacienteServiceJPA servicePaciente;

	@Override
	@Transactional
	public void eliminar(int id_plandieta) {
		repo.deleteById(id_plandieta);
	}

	@Override
	@Transactional(readOnly = true)
	public PlanDieta buscarPorId(int id_plandieta) {
		return repo.findById(id_plandieta).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public PlanDieta buscarPlanActualPaciente(int paciente, LocalDate fecha_actual) {
		return repo.buscarPlanesPacienteActuales(paciente, fecha_actual);
	}

	@Override
	@Transactional
	public void insertar(PlanDieta dietPlan, int id_nutricionista, int id_paciente) {
		/*Nutricionista nutricionista = serviceNutricionsta.buscarPorId(id_nutricionista);
		PacienteDTO paciente = servicePaciente.buscarPorId(id_paciente);
		paciente.getPlanesdieta().add(newPaciente);
		nutricionista.getPlanesDietas().add(newPaciente);
		paciente.setNutricionista(nutricionista);
		nutricionista.getPacientes().add(paciente);
		serviceNutricionsta.guardar(nutricionista);
		TODO: This is a bug,  we should  save only dietPlan, not nutricionista and paciente 
		and upgrade de references in the database for nutricionista and paciente
		.
		
		*/
		
	}

	@Override
	@Transactional(readOnly = true)
	public List<Ingrediente> listadelacompra(int id_paciente, LocalDate fecha_actual) {
		List<Ingrediente> listacompra = new ArrayList<>();
		PlanDieta plandieta = repo.buscarPlanesPacienteActuales(id_paciente, fecha_actual);
		plandieta.getDias().forEach(dia -> {
			dia.getComidas().forEach(comida -> {
				comida.getPlatos().forEach(plato -> {
					plato.getIngredientes().forEach(ingrediente -> {
						Ingrediente existingIngrediente = listacompra.stream()
							.filter(i -> i.getAlimento().getId() == ingrediente.getAlimento().getId())
							.findFirst().orElse(null);
						if (existingIngrediente != null) {
							existingIngrediente.setCantidad(existingIngrediente.getCantidad() + ingrediente.getCantidad());
						} else {
							listacompra.add(ingrediente);
						}
					});
				});
			});
		});
		return listacompra;
	}

	@Override
	@Transactional
	public PlanDieta updatePlan(PlanDieta dieta, int id_nutricionista, int id_paciente) {
		eliminar(dieta.getId());
		insertar(dieta, id_nutricionista, id_paciente);
		return null;
	}

	@Override
	public List<PlanDieta> listarPorNutricionista(int id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'listarPorNutricionista'");
	}

}
// Compare this snippet from src\main\java\com\thunderfat\springboot\backend\model\service\PlanDietaServiceJPA.java: