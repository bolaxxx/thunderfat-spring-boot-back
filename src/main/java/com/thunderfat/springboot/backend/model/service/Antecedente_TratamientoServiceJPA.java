 package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thunderfat.springboot.backend.model.dao.Antecedente_TratamientoRepository;
import com.thunderfat.springboot.backend.model.entity.AntecedenteTratamiento;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Service
public class Antecedente_TratamientoServiceJPA implements IAntecedente_TratamientoService{
	@Autowired
	private Antecedente_TratamientoRepository  antecedentesrepo;
	@Autowired
	private PacienteServiceJPA servicepaciente;

	public List<AntecedenteTratamiento> listarAntecedentes_Tratamiento() {
		List <AntecedenteTratamiento>lista =antecedentesrepo.findAll();
		return lista;
	}

	public AntecedenteTratamiento buscarPorId(int id_antecedente_tratamiento) {
		Optional<AntecedenteTratamiento> op=antecedentesrepo.findById(id_antecedente_tratamiento);
		if(op.isPresent())
			return op.get();
		else
		return null;
	}

	
	public void insertar(AntecedenteTratamiento antecedente_tratamiento, int id_paciente) {
		Paciente pac =servicepaciente.buscarPorId(id_paciente);
		pac.getAntecedentestratamientos().add(antecedente_tratamiento);
		servicepaciente.insertar(pac);
	}

	public void eliminar(int id_antecedente_tratamiento) {
		// TODO Auto-generated method stub
		antecedentesrepo.deleteById(id_antecedente_tratamiento);
	}

	@Override
	public List<AntecedenteTratamiento> buscarPorPaciente(int id_paciente) {
		
		return antecedentesrepo.buscarporPaciente(id_paciente);
	}
	
	

}
