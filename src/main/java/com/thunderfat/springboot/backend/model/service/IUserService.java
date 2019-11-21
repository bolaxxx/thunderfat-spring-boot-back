package com.thunderfat.springboot.backend.model.service;

import com.thunderfat.springboot.backend.model.entity.Usuario;

public interface IUserService {
	 Usuario findByEmail(String email );
	 void deletebyid(int id);

}
