package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.FiltroAlimentario;
@Repository
public interface FiltroAlimentarioRepository extends JpaRepository<FiltroAlimentario, Integer> {
		//List<FiltroAlimentario> findByNutricionista(Nutricionista nutricionista);
	@Query("select p from FiltroAlimentario p where id_nutricionista=?1")
	List<FiltroAlimentario> buscarpornutricionista  (int id_nutriicconista );
}
 