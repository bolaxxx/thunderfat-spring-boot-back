package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thunderfat.springboot.backend.model.dao.Antecedentes_ClinicosRepository;
import com.thunderfat.springboot.backend.model.entity.AntecedentesClinicos;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Service
public class Antecedente_ClinicosServiceJPA implements IAntecedente_ClinicoService{
@Autowired
private Antecedentes_ClinicosRepository repo;
@Autowired
private PacienteServiceJPA servicePaciente;

	@Override
	public List<AntecedentesClinicos> listarAntecedentes() {
		List<AntecedentesClinicos>lista =repo.findAll();
		return lista;
	}

	@Override
	public void insertar(AntecedentesClinicos antecedente, int id_paciente) {
		Paciente pac = this.servicePaciente.buscarPorId(id_paciente);
		pac.getAntecedentesclinicos().add(antecedente);
		this.servicePaciente.insertar(pac);
		
	}

	@Override
	public List<AntecedentesClinicos> listarAntecedenteporPaciente(int id_paciente) {

		
		return repo.buscarporPaciente(id_paciente);

	}

	@Override 
	public void eliminar(int id_antecedente) {
		repo.deleteById(id_antecedente);
		
	}

	@Override
	public AntecedentesClinicos buscarPorID(int id_antecedente) {
		Optional <AntecedentesClinicos>option =repo.findById(id_antecedente);
		if(option.isPresent())
			return option.get();
		else
		// TODO Auto-generated method stub
		return null;
	}

}
