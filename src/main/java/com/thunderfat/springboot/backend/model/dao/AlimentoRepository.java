package com.thunderfat.springboot.backend.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Alimento;

/**
 * Repository interface for performing CRUD operations on Alimento entity.
 */
@Repository
public interface AlimentoRepository extends JpaRepository<Alimento, Integer> {
	
	// ustom query methods here
    
	

}
