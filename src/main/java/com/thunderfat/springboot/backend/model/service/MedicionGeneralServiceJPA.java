package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.Medicion_GeneralRepository;
import com.thunderfat.springboot.backend.model.entity.MedicionGeneral;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Service
public class MedicionGeneralServiceJPA implements IMedicion_GeneralService{
@Autowired
private Medicion_GeneralRepository repo;
@Autowired
private PacienteServiceJPA pacienteservice;

	@Override
	@Transactional
	public MedicionGeneral insertar(MedicionGeneral medicion_general, int id_paciente) {
		Paciente paciente=this.pacienteservice.buscarPorId(id_paciente);
		paciente.getMedicionesgenerales().add(medicion_general);
		pacienteservice.insertar(paciente);
		//repo.save(medicion_general);
		// TODO Auto-generated method stub
		return paciente.getMedicionesgenerales().get(paciente.getMedicionesgenerales().size()-1); 
	}

	@Override
	public void eliminar(int id_medicion_general) {
		repo.deleteById(id_medicion_general);
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<MedicionGeneral> listar() {
		// TODO Auto-generated method stub
		List<MedicionGeneral> lista=repo.findAll();
		return lista;
	}

////	@Override
////	public List<Medicion_General> listarPorPaciente(Paciente paciente) {
////		List<Medicion_General>	lista=repo.findByPacienteOrderByFechaAsc(paciente);
////		
////		// TODO Auto-generated method stub
////		return lista;
//	}

	@Override
	public MedicionGeneral buscarPorID(int id_medicion_general) {
		Optional<MedicionGeneral> op=repo.findById(id_medicion_general);
		if(op.isPresent())
			return op.get();
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public List<Medicion_General> listarPorPacienteFechaReciente(Paciente paciente) {
//		List<Medicion_General>lista =repo.findByPacienteOrderByFechaDesc(paciente);
//		// TODO Auto-generated method stub
//		return lista;
//	}

	@Override
	public double buscarUltimopeso(int id_paciente) {
		
		return repo.ultimoPeso(id_paciente);
	}

	@Override
	@Transactional(readOnly=true)
	public List<MedicionGeneral> listarPorPaciente(int id_paciente) {
		
		return repo.buscarporPaciente(id_paciente);
	}

	@Override
	@Transactional()
	public void update(MedicionGeneral medicion) {
		// TODO Auto-generated method stub
		this.repo.save(medicion);
	}

}
