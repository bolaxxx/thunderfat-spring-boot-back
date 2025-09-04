package com.thunderfat.springboot.backend.model.service;

import java.util.Collections;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.exception.ResourceNotFoundException;
import com.thunderfat.springboot.backend.model.dao.NutricionistaRepository;
import com.thunderfat.springboot.backend.model.dto.NutricionistaDTO;
import com.thunderfat.springboot.backend.model.dto.mapper.NutricionistaMapper;
import com.thunderfat.springboot.backend.model.entity.Nutricionista;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la gestión de nutricionistas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NutricionistaServiceJPA implements INutricionistaService {
	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "nutricionistasBusqueda", key = "#searchTerm")
	@PreAuthorize("permitAll()")
	public List<Nutricionista> searchNutricionistas(String searchTerm) {
		log.debug("Buscando nutricionistas con searchTerm: {}", searchTerm);
		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			return Collections.emptyList();
		}
		// Pageable unpaged for full list
		List<Nutricionista> nutricionistas = repoNutricionista.findByMultipleFieldsSearch(searchTerm, org.springframework.data.domain.Pageable.unpaged()).getContent();
		log.debug("Encontrados {} nutricionistas para la búsqueda: {}", nutricionistas.size(), searchTerm);
		return nutricionistas;
	}
	private final NutricionistaRepository repoNutricionista;

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "nutricionistas")
	@PreAuthorize("permitAll()")
	public List<Nutricionista> listarNutricionista() {
		log.debug("Listando todos los nutricionistas");
		return repoNutricionista.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "nutricionistas", key = "#id_nutricionista")
	@PreAuthorize("hasRole('ROLE_ADMIN') or #id_nutricionista == authentication.principal.id or hasRole('ROLE_NUTRICIONISTA')")
	public Nutricionista buscarPorId(int id_nutricionista) {
		log.debug("Buscando nutricionista por ID: {}", id_nutricionista);
		return repoNutricionista.findById(id_nutricionista)
			.orElseThrow(() -> new ResourceNotFoundException("No se encontró el nutricionista con ID: " + id_nutricionista));
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = {"nutricionistas", "provinciasList", "localidadesList"}, allEntries = true)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void eliminar(Nutricionista nutricionista) {
		log.debug("Eliminando nutricionista con ID: {}", nutricionista.getId());
		if (!repoNutricionista.existsById(nutricionista.getId())) {
			throw new ResourceNotFoundException("No se encontró el nutricionista con ID: " + nutricionista.getId());
		}
		repoNutricionista.delete(nutricionista);
		log.info("Nutricionista con ID: {} eliminado exitosamente", nutricionista.getId());
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "provinciasList")
	@PreAuthorize("permitAll()")
	public List<String> buscarProvincias() {
		log.debug("Buscando todas las provincias de nutricionistas");
		return repoNutricionista.findDistinctProvincias();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "localidadesList", key = "#provincia")
	@PreAuthorize("permitAll()")
	public List<String> buscarLocalidadesporProvincia(String provincia) {
		log.debug("Buscando localidades para la provincia: {}", provincia);
		return repoNutricionista.findDistinctLocalidadesByProvincia(provincia);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "nutricionistasPorLocalidad", key = "#localidad")
	@PreAuthorize("permitAll()")
	public List<Nutricionista> listarNutricionistaporlocalidad(String localidad) {
		log.debug("Listando nutricionistas para la localidad: {}", localidad);
		return repoNutricionista.findByLocalidadIgnoreCaseAndEnabledTrue(localidad);
	}
	
	/**
	 * Guarda o actualiza un nutricionista
	 * 
	 * @param nutri El nutricionista a guardar
	 */
	@Transactional
	@CacheEvict(cacheNames = {"nutricionistas", "provinciasList", "localidadesList", "nutricionistasPorLocalidad"}, allEntries = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or #nutri.id == authentication.principal.id")
	public void guardar(Nutricionista nutri) {
		log.debug("Guardando nutricionista: {}", nutri.getId() > 0 ? nutri.getId() : "nuevo");
		repoNutricionista.save(nutri);
		log.info("Nutricionista guardado exitosamente con ID: {}", nutri.getId());
	}
	
	/**
	 * Busca nutricionistas por nombre, apellidos, email o localidad
	 * 
	 * @param query El texto de búsqueda
	 * @return Lista de nutricionistas que coinciden con la búsqueda
	 */
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "nutricionistasBusqueda", key = "#query")
	@PreAuthorize("permitAll()")
	public List<NutricionistaDTO> buscarNutricionistas(String query) {
		log.debug("Buscando nutricionistas con query: {}", query);
		if (query == null || query.trim().isEmpty()) {
			return Collections.emptyList();
		}
		
		List<Nutricionista> nutricionistas = repoNutricionista.findByMultipleFieldsSearch(query, null)
				.getContent();
		
		log.debug("Encontrados {} nutricionistas para la búsqueda: {}", nutricionistas.size(), query);
		return nutricionistas.stream()
				.map(NutricionistaMapper.INSTANCE::toDTO)
				.toList();
	}
}
