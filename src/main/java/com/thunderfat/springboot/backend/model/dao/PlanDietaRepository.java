package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.PlanDieta;
@Repository
public interface PlanDietaRepository extends JpaRepository<PlanDieta, Integer> {
			//List<PlanDieta> findByNutricionista(Nutricionista nutricionista);
			//List<PlanDieta>  findByPaciente(Paciente paciente);
			@Query(value="Select p from PlanDieta p where id_nutricionista =?1")
			List<PlanDieta> buscarplanesnutricionista(int id_nutricionista);
			@Query(value="Select p from PlanDieta p where id_paciente =?1")
			List<PlanDieta> buscarPorPaciente(int id_paciente);
			@Query(value="SELECT * FROM plan_dieta WHERE id_paciente =?1 AND fechafin >= ?2 ORDER BY fechaini DESC LIMIT 1",nativeQuery=true)
			PlanDieta buscarPlanesPacienteActuales(int id_paciente,LocalDate fechaActual);
			
}
