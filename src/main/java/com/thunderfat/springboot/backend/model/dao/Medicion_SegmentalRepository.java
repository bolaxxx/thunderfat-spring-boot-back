package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;
@Repository
public interface Medicion_SegmentalRepository extends JpaRepository<MedicionSegmental, Integer> {
	//List<Medicion_Segmental>findByPaciente(Paciente paciente);
	@Query("select p from MedicionSegmental p where id_paciente=?1")
	List<MedicionSegmental> buscarporPaciente(int id_paciente);
}
