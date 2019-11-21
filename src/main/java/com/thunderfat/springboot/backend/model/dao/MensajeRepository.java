/**
 * 
 */
package com.thunderfat.springboot.backend.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Mensaje;

/**
 * @author sergio
 *
 */

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

}
