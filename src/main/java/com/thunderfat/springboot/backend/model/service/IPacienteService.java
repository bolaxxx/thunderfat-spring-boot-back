package com.thunderfat.springboot.backend.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thunderfat.springboot.backend.model.entity.Paciente;



public interface IPacienteService {
	List <Paciente> ListarPaciente();
	void insertar(Paciente paciente);
	Paciente buscarPorId(int id_paciente);
	void eliminar(int id_paciente);
List<Paciente> listarPacienteNutrcionista(int id_nutricionista);
List<Paciente> buscarNombreCompleto (int id ,String searchterm );
List<Paciente> buscarPorDni(int id , String Dni);
List<Paciente> buscarPorTelefono(String email, int id );
//	ArrayList<Map>listarPacienteNutricionistaSelect(int id_nutricionista); 
	
}
