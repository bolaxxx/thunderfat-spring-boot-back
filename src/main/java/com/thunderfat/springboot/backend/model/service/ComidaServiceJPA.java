package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.ComidaRepository;
import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

@Service
public class ComidaServiceJPA implements IComidaService {
	@Autowired
	private ComidaRepository repo;
	@Autowired
	private PlanDietaServiceJPA plandietaservice;
	@Autowired
	private PlatoPredeterminadoJPA platoservice;
	@Autowired
	private PacienteServiceJPA pacienteservice;
	@Autowired
	private PlatoPlanDietaJPA platoplanservice;

	@Override
	@Transactional
	public void insertar(Comida comida) {
		repo.save(comida);
	}

	@Override
	@Transactional
	public void eliminar(int id_comida) {
		repo.deleteById(id_comida);
	}

	@Override
	public List<Comida> listaPorPlanDieta(PlanDieta planDieta) {
		// TODO: Implement this method
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Comida buscarPorID(int id_comida) {
		Optional<Comida> op = repo.findById(id_comida);
		return op.orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PlatoPredeterminado> bucarcambios(int id_paciente, int id_plato) {
		List<PlatoPredeterminado> resultados = new ArrayList<>();

		Paciente paciente = pacienteservice.buscarPorId(id_paciente);
		PlanDieta plandieta = plandietaservice.buscarPlanActualPaciente(id_paciente, LocalDate.now());
		PlatoPlanDieta plato = platoplanservice.buscarPorId(id_plato);
		List<PlatoPredeterminado> listacambiosposible = platoservice.listarPorNutricionista(paciente.getNutricionista().getId());

		for (PlatoPredeterminado p : listacambiosposible) {
			if (!isIngredientInPlan(p, plandieta) && isSustituible(plato, p)) {
				resultados.add(p);
			}
		}

		return resultados;
	}

	private boolean isIngredientInPlan(PlatoPredeterminado plato, PlanDieta plandieta) {
		return plato.getIngredientes().stream()
				.anyMatch(ingrediente -> plandieta.getFiltrosaplicado().getAlimentos().contains(ingrediente.getAlimento()));
	}

	private boolean isSustituible(PlatoPlanDieta marcado, PlatoPredeterminado posible) {
		double maximo = (marcado.getKcaltotales() * 110) / 100;
		double minimo = (marcado.getKcaltotales() * 90) / 100;
		return posible.getKcaltotales() < maximo && posible.getKcaltotales() > minimo;
	}
}
