package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.PlanDietaRepository;
import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.DiaDieta;
import com.thunderfat.springboot.backend.model.entity.Ingrediente;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;

@Service
public class PlanDietaServiceJPA implements IPlanDietaService {
	@Autowired
	private PlanDietaRepository repo;
	@Autowired
	private NutricionistaServiceJPA serviceNutricionsta;
	@Autowired
	private PacienteServiceJPA servicePaciente;

	@Override
	@Transactional()
	public void eliminar(int id_plandieta) {
		// TODO Auto-generated method stub
		repo.deleteById(id_plandieta);
	}

	@Override
	@Transactional(readOnly = true)
	public PlanDieta buscarPorId(int id_plandieta) {
		// TODO Auto-generated method stub
		return repo.findById(id_plandieta).orElse(null);

	}

	// @Override
	// public List<PlanDieta> buscarPorNutricionista(Nutricionista nutricionista) {
	//
	// // TODO Auto-generated method stub
	// return repo.findByNutricionista(nutricionista);
	// }

	// @Override
	// public List<PlanDieta> buscarPorPaciente(Paciente paciente) {
	// // TODO Auto-generated method stub
	// return repo.findByPaciente(paciente);
	// }

	@Override
	@Transactional(readOnly = true)
	public PlanDieta buscarPlanActualPaciente(int paciente, LocalDate fecha_actual) {
		// TODO Auto-generated method stub

		return repo.buscarPlanesPacienteActuales(paciente, fecha_actual);
	}

	@Override
	@Transactional()
	public void insertar(PlanDieta newPaciente, int id_nutricionista, int id_paciente) {
		// TODO Auto-generated method stubç
		Nutricionista nutricionista = this.serviceNutricionsta.buscarPorId(id_nutricionista);
		Paciente paciente = this.servicePaciente.buscarPorId(id_paciente);
		paciente.getPlanesdieta().add(newPaciente);
		nutricionista.getPlanesdietas().add(newPaciente);
		paciente.setNutricionista(nutricionista);
		nutricionista.getPacientes().add(paciente);

		// nutricionista.getPacientes().set(nutricionista.getPacientes().indexOf(paciente),
		// paciente)
		serviceNutricionsta.guardar(nutricionista);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PlanDieta> listarPorNutricionista(int id) {
		// TODO Auto-generated method stub
		// repo.
		// this.
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Ingrediente> listadelacompra(int id_paciente, LocalDate fecha_actual) {
		// TODO Auto-generated method stub

		List<Ingrediente> listacompra = new ArrayList<Ingrediente>();
		PlanDieta plandieta = repo.buscarPlanesPacienteActuales(id_paciente, fecha_actual);
		plandieta.getDias().stream().forEach((p) -> {
			p.getComidas().stream().forEach((m) -> {
				m.getPlatos().stream().forEach((n) -> {
					n.getIngredientes().stream().forEach((o) -> {

						int i = 0;
						Ingrediente IngedienteToadd = new Ingrediente();
						if (listacompra.size() == 0) {

							IngedienteToadd.setAlimento(o.getAlimento());
							IngedienteToadd.setCantidad(o.getCantidad());
							listacompra.add(IngedienteToadd);
						} else {
							while (i < listacompra.size()) {
								if (listacompra.get(i).getAlimento().getId() == o.getAlimento().getId()) {

									listacompra.get(i).setCantidad(o.getCantidad() + listacompra.get(i).getCantidad());
									i = listacompra.size();
								} else {
									if (i == listacompra.size() - 1) {

										IngedienteToadd.setAlimento(o.getAlimento());
										IngedienteToadd.setCantidad(o.getCantidad());
										listacompra.add(IngedienteToadd);
									}
									i++;
								}
							}
						}

					});
				});
			});
		});
		return listacompra;
	}

	@Override
	@Transactional()
	public PlanDieta updatePlan(PlanDieta dieta, int id_nutricionista, int id_paciente) {
		PlanDieta dietaedited = new PlanDieta();
		dietaedited.setCalrangomax(dieta.getCalrangomax());
		dietaedited.setCalrangomin(dieta.getCalrangomin());
		dietaedited.setIngestacaldiaria(dieta.getIngestacaldiaria());
		dietaedited.setFechafin(dieta.getFechafin());
		dietaedited.setFechaini(dieta.getFechaini());
		dietaedited.setFiltrosaplicado(dieta.getFiltrosaplicado());
		dietaedited.setComidasdiarias(dieta.getComidasdiarias());
		dietaedited.setRepartoglucidodiario(dieta.getRepartoglucidodiario());
		dietaedited.setRepartolipidodiario(dieta.getRepartolipidodiario());
		dietaedited.setRepartoprotidodiario(dieta.getRepartoprotidodiario());
		Comida comida = new Comida();
		DiaDieta dia = new DiaDieta();
		Ingrediente ingrediente = new Ingrediente();
		PlatoPlanDieta plato = new PlatoPlanDieta();
		for (DiaDieta p : dieta.getDias()) {
			

			dia.setFecha(p.getFecha());
			for (Comida c : p.getComidas()) {
				
				comida.setHora(c.getHora());
				for (PlatoPlanDieta d : c.getPlatos()) {
					
					plato.setGrasastotales(d.getGrasastotales());
					plato.setHidratostotales(d.getHidratostotales());
					plato.setProteinastotales(d.getProteinastotales());
					plato.setKcaltotales(d.getKcaltotales());
					plato.setNombre(d.getNombre());
					// plato.setReceta(d.getReceta());
					for (Ingrediente i : d.getIngredientes()) {
						
						ingrediente.setAlimento(i.getAlimento());
						ingrediente.setProteinastotales(i.getProteinastotales());
						ingrediente.setGrasastotales(i.getGrasastotales());
						ingrediente.setKcaltotales(i.getKcaltotales());
						ingrediente.setHidratostotales(i.getHidratostotales());
						ingrediente.setCantidad(i.getCantidad());
						plato.getIngredientes().add(ingrediente);
					}
					;
					comida.getPlatos().add(plato);
				}
				;
				dia.getComidas().add(comida);
			}
			;
			dietaedited.getDias().add(dia);
		}
		;

		eliminar(dieta.getId());

		insertar(dietaedited, id_nutricionista, id_paciente);
		return null;
	}

}
