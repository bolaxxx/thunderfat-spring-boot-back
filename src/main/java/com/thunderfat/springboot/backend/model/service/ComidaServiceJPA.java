package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.thunderfat.springboot.backend.model.dao.ComidaRepository;
import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.Ingrediente;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.entity.Plato;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;
import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

@Service
public class ComidaServiceJPA implements IComidaService {
	@Autowired
	private ComidaRepository repo;
	@Autowired
	private PlanDietaServiceJPA plandietaservice;
	@Autowired
	private NutricionistaServiceJPA nutricionistaservice;
	@Autowired
	private PlatoPredeterminadoJPA platoservice;
	@Autowired
	private PacienteServiceJPA pacienteservice;
	@Autowired
	private PlatoPlanDietaJPA platoplanservice;

	@Override
	@Transactional()
	public void insertar(Comida comida) {
		// TODO Auto-generated method stub
		repo.save(comida);

	}

	@Override
	@Transactional()
	public void eliminar(int id_comida) {
		// TODO Auto-generated method stub
		repo.deleteById(id_comida);
	}

	@Override
	public List<Comida> listaPorPlanDieta(PlanDieta planDieta) {
		// TODO Auto-generated method stub

		return null;// repo.findByPlandieta(planDieta);
	}

	@Override
	@Transactional(readOnly = true)
	public Comida buscarPorID(int id_comida) {
		Optional<Comida> op = repo.findById(id_comida);
		if (op.isPresent())
			return op.get();

		// TODO Auto-generated method stub
		return null;
	}
	@Override
	@Transactional(readOnly=true)
	public List<PlatoPredeterminado> bucarcambios(int id_paciente, int id_plato) {
		List<PlatoPredeterminado> resultados = new ArrayList<PlatoPredeterminado>();
		int i = 0;

		Paciente paciente = this.pacienteservice.buscarPorId(id_paciente);
		PlanDieta plandieta = this.plandietaservice.buscarPlanActualPaciente(id_paciente, LocalDate.now());
		PlatoPlanDieta plato = this.platoplanservice.buscarPorId(id_plato);
		List<PlatoPredeterminado> listacambiosposible = this.platoservice
				.listarPorNutricionista(paciente.getNutricionista().getId());
		for (PlatoPredeterminado p : listacambiosposible) {
			i = 0;
			while (i < p.getIngredientes().size()) {
				Ingrediente ingrediente = p.getIngredientes().get(i);
				if (plandieta.getFiltrosaplicado().getAlimentos().contains(ingrediente.getAlimento())) {
					i = p.getIngredientes().size() + 1;
				} else {
					if (i == p.getIngredientes().size() - 1) {
						if (esSustituible(plato, p)) {
							resultados.add(p);
						}
						i++;

					}
				}
			}
		}
		// Paciente paciente =this.pacienteservice.buscarPorId(Idpaciente);
		// List<Plato > listacambiosposible
		// aciente.getNutricionista().getPlatos();
		// PlanDieta plandieta=this.plandietaservice.buscarPlanActualPaciente(paciente,
		// fecha);
		return resultados;
	}

	public boolean esSustituible(PlatoPlanDieta marcado, PlatoPredeterminado posible) {
		double maximo = (marcado.getKcaltotales() * 110) / 100;
		double minimo = (marcado.getKcaltotales() * 90) / 100;
		if (posible.getKcaltotales() < maximo && posible.getKcaltotales() > minimo)
			return true;
		else
			return false;
	}

	
}
