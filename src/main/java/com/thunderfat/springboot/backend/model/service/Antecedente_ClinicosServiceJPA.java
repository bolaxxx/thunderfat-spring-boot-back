package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;

@Repository
public interface Antecedente_ClinicosServiceJPA extends JpaRepository<AntecedentesClinicos, Integer> {

    @Query("SELECT p FROM AntecedentesClinicos p WHERE p.paciente.id = ?1")
    List<AntecedentesClinicos> buscarPorIdPaciente(int idPaciente);

    // Agrega aquí más consultas personalizadas según tus necesidades

}
