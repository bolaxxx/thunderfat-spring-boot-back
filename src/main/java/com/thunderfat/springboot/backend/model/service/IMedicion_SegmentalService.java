package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;

public interface IMedicion_SegmentalService {
	void insertar(MedicionSegmental medicion_general, int id_paciente);
	void eliminar(int id_medicion_general);
	List<MedicionSegmental> listar();
	List<MedicionSegmental> listarPorPaciente(int id_paciente);
	MedicionSegmental buscarPorID(int id_medicion_general);
	void actualizar(MedicionSegmental medicion, int id_paciente);
}
