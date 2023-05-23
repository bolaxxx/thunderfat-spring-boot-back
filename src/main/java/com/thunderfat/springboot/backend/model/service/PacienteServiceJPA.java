package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.NutricionistaRepository;
import com.thunderfat.springboot.backend.model.dao.PacienteRepository;
import com.thunderfat.springboot.backend.model.dto.PacienteDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.PacienteMapper;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;
import com.thunderfat.springboot.backend.model.entity.Paciente;

@Service
public class PacienteServiceJPA implements IPacienteService {
    @Autowired
    private PacienteRepository repo;

    @Autowired
    private NutricionistaRepository nutrirepo;

    @Autowired
    private PacienteMapper pacienteMapper;

    @Transactional(readOnly = true)
    @Override
    public List<PacienteDTO> listarPacienteNutrcionista(int id_nutricionista) {
        List<Paciente> pacientes = repo.buscarPorNutricionista(id_nutricionista);
        return pacientes.stream()
                .map(pacienteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PacienteDTO> buscarNombreCompleto(int id, String searchTerm) {
        List<Paciente> pacientes = repo.findBySearchString(searchTerm, id);
        return pacientes.stream()
                .map(pacienteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PacienteDTO> buscarPorDni(int id, String dni) {
        List<Paciente> pacientes = repo.buscardni(dni, id);
        return pacientes.stream()
                .map(pacienteMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PacienteDTO> buscarPorTelefono(String telefono, int id) {
        List<Paciente> pacientes = repo.buscarPorTelefono(telefono, id);
        return pacientes.stream()
                .map(pacienteMapper::toDto)
                .collect(Collectors.toList());
    }

    

    @Transactional(readOnly = true)
    @Override
    public PacienteDTO buscarPorId(int id_paciente) {
        Optional<Paciente> optional = repo.findById(id_paciente);
        return optional.map(pacienteMapper::toDto).orElse(null);
    }

    @Transactional
    @Override
    public void eliminar(int id_paciente) {
        Paciente pac = repo.findById(id_paciente).orElse(null);
        if (pac != null) {
            Nutricionista nutricionista = nutrirepo.findById(pac.getNutricionista().getId()).orElse(null);
            if (nutricionista != null) {
                pac.getRoles().clear();
                pac.getCitas().clear();
                pac.getPlanesdieta().clear();
                pac.getAntecedentesclinicos().clear();
                pac.getAntecedentestratamientos().clear();
                nutricionista.removePaciente(pac);
                nutrirepo.save(nutricionista);
            }
        }
    }

	@Override
	public List<PacienteDTO> ListarPaciente() {
		return repo.findAll().stream().map(pacienteMapper::toDto).collect(Collectors.toList());
		
	}

	@Override
	public void insertar(PacienteDTO paciente) {
		repo.save(pacienteMapper.toEntity(paciente));
    }


    // Resto de métodos omitidos por concisión
}
