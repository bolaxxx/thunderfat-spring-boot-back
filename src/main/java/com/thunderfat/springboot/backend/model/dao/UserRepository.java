package com.thunderfat.springboot.backend.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thunderfat.springboot.backend.model.entity.Usuario;
@Repository
public interface UserRepository extends JpaRepository<Usuario	, Integer> {
	public  Usuario findByEmail(String email);

}
