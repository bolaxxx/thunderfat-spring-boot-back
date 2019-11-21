package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;
@Repository
public interface Antecedentes_ClinicosRepository extends JpaRepository<AntecedentesClinicos, Integer> {
//List<Antecedentes_Clinicos> findByPaciente(Paciente paciente);
	@Query("select p from AntecedentesClinicos p where id_paciente=?1")
	List<AntecedentesClinicos> buscarporPaciente(int id_paciente);
}
