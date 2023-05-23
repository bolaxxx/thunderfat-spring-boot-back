package com.thunderfat.springboot.backend.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.Medicion_SegmentalRepository;
import com.thunderfat.springboot.backend.model.entity.MedicionSegmental;
import com.thunderfat.springboot.backend.model.entity.Paciente;

import java.util.List;
import java.util.Optional;

@Service
public class Medicion_SegmentalServiceJPA implements IMedicion_SegmentalService {

    @Autowired
	private final Medicion_SegmentalRepository repo;
	
    @Autowired
    private final PacienteServiceJPA servicePaciente;


    public Medicion_SegmentalServiceJPA(Medicion_SegmentalRepository repo, PacienteServiceJPA servicePaciente) {
        this.repo = repo;
        this.servicePaciente = servicePaciente;
    }

    @Override
    @Transactional
    public void eliminar(int id_medicion_segmental) {
        repo.deleteById(id_medicion_segmental);
    }

    @Override
    @Transactional
    public void insertar(MedicionSegmental medicion_general, int id_paciente) {
        Paciente paciente = servicePaciente.buscarPorId(id_paciente);
        paciente.getMedicionessegmentales().add(medicion_general);
        servicePaciente.insertar(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicionSegmental> listar() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicionSegmental> listarPorPaciente(int id_paciente) {
        return repo.buscarporPaciente(id_paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicionSegmental buscarPorID(int id_medicion_general) {
        Optional<MedicionSegmental> optional = repo.findById(id_medicion_general);
        return optional.orElse(null);
    }

	@Override
	public void actualizar(MedicionSegmental medicion, int id_paciente) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'actualizar'");
	}
}
