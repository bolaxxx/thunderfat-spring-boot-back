package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Paciente;
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
	@Query(value="select * from Paciente  FULL INNER JOIN cita on id = id_paciente  where id_cita=? ",nativeQuery=true)
	List<Paciente> buscarpaciente(int id );
	@Query("select p from Paciente p where id_nutricionista=?1")
	List<Paciente> buscarPorNutricionista(int id);
//	@Query("select p from Paciente p where p.nutricionista = ?1 ")
//	List<Paciente> buscarporNutricionista(Nutricionista id_nutricionista);
	@Query("select e from Paciente e where  dni like %?1% and id_nutricionista=?2")
	List<Paciente>  buscardni(String  dni,int id);
	@Query("select e from Paciente e where  telefono like %?1% and id_nutricionista=?2")
	List<Paciente> buscarporEmail(String email,int id );
	@Query("select e from Paciente e where  concat(trim(nombre), trim(apellidos)) like %?1% and id_nutricionista=?2")
	List<Paciente> findBySearchString(String searchString, int idnutrionista);
	List<Paciente> findNutricionistaById(int id );
	
}
