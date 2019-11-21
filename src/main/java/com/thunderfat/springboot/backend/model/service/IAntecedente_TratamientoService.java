package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;

public interface IAntecedente_TratamientoService {
	List<AntecedenteTratamiento>listarAntecedentes_Tratamiento();
	AntecedenteTratamiento buscarPorId(int id_antecedente_tratamiento);
	List<AntecedenteTratamiento> buscarPorPaciente(int id_paciente);
	void insertar(AntecedenteTratamiento antecedente_tratamiento,int id_paciente);
	void eliminar(int id_antecedente_tratamiento);

}
