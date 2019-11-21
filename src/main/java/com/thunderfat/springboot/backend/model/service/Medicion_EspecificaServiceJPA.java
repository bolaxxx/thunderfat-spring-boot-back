package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.Medicion_EspecificaRepository;
import com.thunderfat.springboot.backend.model.entity.MedicionEspecifica;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Service
public class Medicion_EspecificaServiceJPA implements IMedicion_EspecificaService {
@Autowired 
private Medicion_EspecificaRepository repo;
@Autowired
private PacienteServiceJPA servicepaciente;

	@Override
	@Transactional
	public void eliminar(int id_medicion_espeficica) {
		repo.deleteById(id_medicion_espeficica);
		
	}

	@Override
	@Transactional(readOnly=true)
	public MedicionEspecifica buscarPorId(int id_medicion_especifica) {
		Optional<MedicionEspecifica>op=repo.findById(id_medicion_especifica);
		if(op.isPresent())
			return op.get();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public List<MedicionEspecifica> listar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<MedicionEspecifica> listarPorPaciente(int id_paciente) {
		// TODO Auto-generated method stub
		
		return repo.buscarporPaciente(id_paciente);
	}

	@Override
	@Transactional
	public void insertar(MedicionEspecifica medicion, int id_paciente) {
		// TODO Auto-generated method stub
		Paciente pac =this.servicepaciente.buscarPorId(id_paciente);
		pac.getMedicionesespecificas().add(medicion);
		this.servicepaciente.insertar(pac);
	}


}
