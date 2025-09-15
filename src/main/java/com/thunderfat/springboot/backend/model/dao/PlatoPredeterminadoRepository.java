package com.thunderfat.springboot.backend.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.PlatoPredeterminado;

/**
 * Repository for PlatoPredeterminado entities.
 * Provides methods for CRUD operations and custom queries.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Repository
public interface PlatoPredeterminadoRepository extends JpaRepository<PlatoPredeterminado, Integer>, 
                                                       JpaSpecificationExecutor<PlatoPredeterminado> {
    
    /**
     * Find all predetermined dishes by nutritionist ID.
     * Uses the proper relationship between PlatoPredeterminado and Nutricionista.
     * 
     * @param id_nutricionista The ID of the nutritionist
     * @return List of PlatoPredeterminado entities
     */
    @RestResource(path = "byNutricionistaIdQuery", rel = "byNutricionistaIdQuery")
    @Query("SELECT p FROM PlatoPredeterminado p WHERE p.nutricionista.id = :id_nutricionista")
    List<PlatoPredeterminado> listapornutricionista(@Param("id_nutricionista") int id_nutricionista);
    
    /**
     * Modern method to find all dishes by nutritionist ID.
     * This uses Spring Data JPA method naming conventions instead of @Query.
     * 
     * @param nutricionistaId The nutritionist ID
     * @return List of dishes
     */
    @RestResource(path = "byNutricionistaIdConvention", rel = "byNutricionistaIdConvention")
    List<PlatoPredeterminado> findByNutricionistaId(Integer nutricionistaId);
    
    /**
     * Find predetermined dishes by maximum calories.
     * This is a temporary implementation until proper filtering is added.
     * 
     * @param paciente The patient ID
     * @param kcal The maximum calories
     * @return List of PlatoPredeterminado entities
     */
    @Query("SELECT p FROM PlatoPredeterminado p WHERE p.kcaltotales <= :kcal")
    List<PlatoPredeterminado> listarposibles(@Param("paciente") int paciente, @Param("kcal") double kcal);
    
    // Page<PlatoPredeterminado> findAll(Pageable pageable) is already defined in JpaRepository
}
