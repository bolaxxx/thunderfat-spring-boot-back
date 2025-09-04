package com.thunderfat.springboot.backend.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;

/**
 * Repository for {@link PlatoPlanDieta} entities.
 * Provides standard CRUD operations and custom queries.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Repository
public interface PlatoPlanDietaRepository extends JpaRepository<PlatoPlanDieta, Integer> {
    
    /**
     * Find PlatoPlanDieta entries associated with a specific Comida
     * 
     * @param comidaId The ID of the Comida
     * @param pageable Pagination information
     * @return A page of PlatoPlanDieta entities
     */
    @Query("SELECT p FROM PlatoPlanDieta p JOIN Comida c ON p.id = c.id WHERE c.id = :comidaId")
    Page<PlatoPlanDieta> findByComidaId(@Param("comidaId") Integer comidaId, Pageable pageable);
}
