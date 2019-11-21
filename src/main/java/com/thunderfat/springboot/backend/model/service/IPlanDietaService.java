package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.List;

import com.thunderfat.springboot.backend.model.entity.Ingrediente;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;

public interface IPlanDietaService  {

	//void insertar(PlanDieta plandieta);
	void eliminar(int id_plandieta);
	PlanDieta buscarPorId(int id_plandieta);
	//List<PlanDieta> buscarPorNutricionista(Nutricionista nutricionista);
	//List<PlanDieta> buscarPorPaciente(Paciente paciente);
	PlanDieta buscarPlanActualPaciente(int  paciente, LocalDate fecha_actual);
	void insertar(PlanDieta newPaciente, int id_nutricionista, int id_paciente);
	List<PlanDieta> listarPorNutricionista(int id);
	List<Ingrediente> listadelacompra(int id_paciente, LocalDate fecha_actual);
	
	PlanDieta updatePlan(PlanDieta dieta, int id_nutricionista, int id_paciente);
}
