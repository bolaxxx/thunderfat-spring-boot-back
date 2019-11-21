package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;
@Repository
public interface PlatoPredeterminadoRepository extends JpaRepository<PlatoPredeterminado, Integer> {
	//List<PlatoPredeterminado>findByCreador(Nutricionista creador);
	@Query(value="select p from PlatoPredeterminado p where id_nutricionista=?1")
	List<PlatoPredeterminado>listapornutricionista(int id_nutricionista);
	@Query(value="select p from PlatoPredeterminado p ")
	List<PlatoPredeterminado> listarposibles(int paciente ,double kcal);

}
