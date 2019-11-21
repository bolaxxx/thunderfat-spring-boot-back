package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;

public interface IMedicion_GeneralService {
	
	MedicionGeneral insertar(MedicionGeneral medicion_general, int id_paciente);
	void eliminar(int  id_medicion_general);
	List<MedicionGeneral>listar();
	List<MedicionGeneral>listarPorPaciente(int id_paciente);
	//List<Medicion_General>listarPorPacienteFechaReciente(Paciente paciente);
	MedicionGeneral buscarPorID(int id_medicion_general);
	void update(MedicionGeneral medicion);
	double buscarUltimopeso(int id_paciente);

}
