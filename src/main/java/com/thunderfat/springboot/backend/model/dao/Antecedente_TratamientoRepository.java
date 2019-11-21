package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;
@Repository
public interface Antecedente_TratamientoRepository extends JpaRepository<AntecedenteTratamiento, Integer> {
	//List <Antecedente_Tratamiento> findByPaciente(Paciente paciente);
	@Query("select p from AntecedenteTratamiento p where id_paciente=?1")
	List<AntecedenteTratamiento> buscarporPaciente(int id_paciente);
}
