package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Comida;
import com.thunderfat.springboot.backend.model.entity.PlanDieta;
@Repository
public interface ComidaRepository extends JpaRepository<Comida, Integer> {
	//List<Comida> findByPlandieta(PlanDieta plandieta);

}
