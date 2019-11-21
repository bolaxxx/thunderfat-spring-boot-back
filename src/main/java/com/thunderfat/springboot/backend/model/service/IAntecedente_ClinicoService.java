package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;

public interface IAntecedente_ClinicoService {
	
	
	List<AntecedentesClinicos>listarAntecedentes();
	void insertar(AntecedentesClinicos antecedente,int id_paciente);

	void eliminar(int id_antecedente);
	AntecedentesClinicos buscarPorID(int id_antecedente);
	List<AntecedentesClinicos> listarAntecedenteporPaciente(int id_paciente);

}
