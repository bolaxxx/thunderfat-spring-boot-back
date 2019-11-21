package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
@Repository
public interface Medicion_GeneralRepository extends JpaRepository<MedicionGeneral, Integer> {
	// List<Medicion_General> findByPacienteOrderByFechaAsc(Paciente paciente);
	// List<Medicion_General>findByPacienteOrderByFechaDesc(Paciente paciente);
	@Query("select p from MedicionGeneral p where id_paciente=?1")
	List<MedicionGeneral> buscarporPaciente(int id_paciente);
	 @Query(value="SELECT peso_actual FROM medicion_general WHERE fecha IN ( SELECT MAX( fecha ) FROM medicion_general WHERE id_paciente =?1 GROUP BY id_medicion_general ) ORDER BY fecha DESC LIMIT 1",nativeQuery=true)
	 double ultimoPeso(int id_paciente);
}
