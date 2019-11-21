package com.thunderfat.springboot.backend.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Plato;
@Repository
public interface PlatoRepository extends JpaRepository<Plato, Integer> {

}
