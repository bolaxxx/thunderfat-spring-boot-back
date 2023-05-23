package com.thunderfat.springboot.backend.model.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thunderfat.springboot.backend.model.entity.Cita;
import com.thunderfat.springboot.backend.model.entity.Paciente;

public interface ICitaService {
	List <Cita> ListarCita();
	List<Cita>	ListarPorPaciente(Paciente paciente);
	//List<Cita>  ListarPorNutricionista(Nutricionista nutricionista);
	void insertar(Cita cita,int id_nutricionista);
	void eliminar(int id_cita);
	Cita buscarPorID(int id_cita);
	ArrayList<Map> ListarPorPacienteEntreFechas(int   nutricionista,LocalDate start,LocalDate end);
	Cita buscarproximacita (int id_paciente,LocalDate start);
}
