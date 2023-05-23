package com.thunderfat.springboot.backend.model.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Cita;
@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
	//List<Cita> findByPaciente(Paciente paciente);
	@Query(value="select * from cita WHERE id_nutricionista=?1 AND fechaini BETWEEN ?2 AND ?3",nativeQuery=true)
	List<Cita> encontrarCitasNutricionistaFechas(int id_nutricionista,LocalDate start,LocalDate end);
	@Query(value="select * from Cita where id_paciente=?1 and fechaini>=?2  ORDER BY fechaini DESC LIMIT 1",nativeQuery=true)
	Cita proximacita(int id_paciente , LocalDate  star);

}
