package com.thunderfat.springboot.backend.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    // Custom queries can be added here if needed
}
