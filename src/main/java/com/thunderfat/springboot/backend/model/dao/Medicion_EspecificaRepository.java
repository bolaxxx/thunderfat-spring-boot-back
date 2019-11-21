package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;
@Repository
public interface Medicion_EspecificaRepository extends JpaRepository<MedicionEspecifica, Integer> {
	//List<Medicion_Especifica>findByPaciente(Paciente paciente);
	@Query("select p from MedicionEspecifica p where id_paciente=?1")
	List<MedicionEspecifica> buscarporPaciente(int id_paciente);
}
