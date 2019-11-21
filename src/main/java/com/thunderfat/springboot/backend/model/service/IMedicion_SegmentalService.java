package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;

public interface IMedicion_SegmentalService {

	void insertar(MedicionSegmental medicion_general, int id_paciente);
	void eliminar(int  id_medicion_general);
	List<MedicionSegmental>listar();
	List<MedicionSegmental>listarPorPaciente(int id_paciente);
	//List<Medicion_General>listarPorPacienteFechaReciente(Paciente paciente);
	MedicionSegmental buscarPorID(int id_medicion_general);
//List<Medicion_Segmental> buscarPorPaciente(Paciente paciente);
}
