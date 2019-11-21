package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;
import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.entity.Paciente;

public interface IMedicion_EspecificaService {
	void insertar(MedicionEspecifica medicion ,int id_paciente);
	void eliminar(int id_medicion_espeficica);
	MedicionEspecifica buscarPorId(int id_medicion_especifica);
	List<MedicionEspecifica>listar();
	List<MedicionEspecifica>listarPorPaciente(int id_paciente);
}
