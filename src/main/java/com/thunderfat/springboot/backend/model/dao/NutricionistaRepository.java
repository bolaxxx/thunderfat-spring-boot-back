package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Nutricionista;

@Repository
public interface NutricionistaRepository extends JpaRepository<Nutricionista, Integer>{
	@Query("select distinct provincia from Nutricionista")
	List<String> findDistinctProvincia();
	@Query ("select distinct n.localidad from Nutricionista  n  where n.provincia=?1")
	List<String> findDistinctLocalidadByProvincia(String provincia);
	List<Nutricionista> findByLocalidad(String localidad);
}
