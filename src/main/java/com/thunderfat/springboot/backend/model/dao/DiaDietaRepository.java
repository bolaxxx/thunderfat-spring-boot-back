package com.thunderfat.springboot.backend.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.DiaDieta;
@Repository
public interface DiaDietaRepository extends JpaRepository<DiaDieta, Integer> {

}
