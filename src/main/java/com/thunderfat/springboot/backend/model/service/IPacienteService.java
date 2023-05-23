package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.dto.PacienteDTO;



public interface IPacienteService {
	List<PacienteDTO> ListarPaciente();
	void insertar(PacienteDTO paciente);
	PacienteDTO buscarPorId(int id_paciente);
	void eliminar(int id_paciente);
	List<PacienteDTO> listarPacienteNutrcionista(int id_nutricionista);
	List<PacienteDTO> buscarNombreCompleto (int id ,String searchterm );
	List<PacienteDTO> buscarPorDni(int id , String Dni);
	List<PacienteDTO> buscarPorTelefono(String email, int id );
//	ArrayList<Map>listarPacienteNutricionistaSelect(int id_nutricionista); 
	
}
