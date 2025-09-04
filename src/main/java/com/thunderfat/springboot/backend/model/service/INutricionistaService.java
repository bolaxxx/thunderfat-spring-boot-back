package com.thunderfat.springboot.backend.model.service;

import java.util.List;

import com.thunderfat.springboot.backend.model.entity.Nutricionista;

/**
 * Interfaz que define las operaciones para el servicio de nutricionistas
 */
public interface INutricionistaService {
	/**
	 * Busca nutricionistas por nombre, apellidos, email, teléfono o número de colegiado profesional
	 * @param searchTerm término de búsqueda
	 * @return lista de nutricionistas que coinciden
	 */
	List<Nutricionista> searchNutricionistas(String searchTerm);
	
	/**
	 * Lista todos los nutricionistas
	 * 
	 * @return Lista de nutricionistas
	 */
	List<Nutricionista> listarNutricionista();
	
	/**
	 * Busca un nutricionista por su ID
	 * 
	 * @param id_nutricionista ID del nutricionista a buscar
	 * @return Nutricionista encontrado o lanza excepción si no existe
	 */
	Nutricionista buscarPorId(int id_nutricionista);
	
	/**
	 * Elimina un nutricionista
	 * 
	 * @param nutricionista El nutricionista a eliminar
	 */
	void eliminar(Nutricionista nutricionista);
	
	/**
	 * Busca todas las provincias donde hay nutricionistas
	 * 
	 * @return Lista de provincias
	 */
	List<String> buscarProvincias();
	
	/**
	 * Busca localidades para una provincia específica
	 * 
	 * @param provincia Nombre de la provincia
	 * @return Lista de localidades en esa provincia
	 */
	List<String> buscarLocalidadesporProvincia(String provincia);
	
	/**
	 * Lista nutricionistas por localidad
	 * 
	 * @param localidad Nombre de la localidad
	 * @return Lista de nutricionistas en esa localidad
	 */
	List<Nutricionista> listarNutricionistaporlocalidad(String localidad);
}
