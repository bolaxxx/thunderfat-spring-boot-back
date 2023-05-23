package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;

@Repository
public interface Antecedentes_ClinicosRepository extends JpaRepository<AntecedentesClinicos, Integer> {

    @Query("SELECT p FROM AntecedentesClinicos p WHERE id_paciente = ?1")
    List<AntecedentesClinicos> buscarPorPaciente(int id_paciente);

    // Agrega aquí más consultas personalizadas según tus necesidades

}
