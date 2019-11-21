package com.thunderfat.springboot.backend.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.PlatoPlanDieta;
@Repository
public interface PlatoPlanDietaRepository extends JpaRepository<PlatoPlanDieta, Integer> {

}
