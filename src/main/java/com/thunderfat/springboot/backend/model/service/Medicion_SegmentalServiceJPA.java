package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.Medicion_SegmentalRepository;
import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Service
public class Medicion_SegmentalServiceJPA implements IMedicion_SegmentalService{
@Autowired
private Medicion_SegmentalRepository repo;
@Autowired
private PacienteServiceJPA servicePaciente;
	@Override
	@Transactional
	public void eliminar(int id_medicion_segmental) {
		repo.deleteById(id_medicion_segmental);
		// TODO Auto-generated method stub
		
	}
	@Override
	@Transactional
	public void insertar(MedicionSegmental medicion_general, int id_paciente) {
		// TODO Auto-generated method stub
		Paciente paciente=this.servicePaciente.buscarPorId(id_paciente);
		paciente.getMedicionessegmentales().add(medicion_general);
		this.servicePaciente.insertar(paciente);
	}
	@Override
	public List<MedicionSegmental> listar() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<MedicionSegmental> listarPorPaciente(int id_paciente) {
		// TODO Auto-generated method stub
		return repo.buscarporPaciente(id_paciente);
	}
	@Override
	public MedicionSegmental buscarPorID(int id_medicion_general) {
		// TODO Auto-generated method stub
		return repo.findById(id_medicion_general).orElse(null);
	}

	
}
