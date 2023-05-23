package com.thunderfat.springboot.backend.model.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thunderfat.springboot.backend.model.dao.UserRepository;
import com.thunderfat.springboot.backend.model.entity.Usuario;

@Service
public class UserServiceJPA implements UserDetailsService, IUserService {
	private Logger logger = LoggerFactory.getLogger(UserServiceJPA.class);
	@Autowired
	private UserRepository repo;

	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario user = repo.findByEmail(username);
		if(user==null) {
			logger.error("no se ha encontrado  el usuario  error en el login");
			throw new UsernameNotFoundException("no se a encotrado un usuario con el siguiente mail"+username);
		}
		List<GrantedAuthority> authorities= user.getRoles().stream().map(role->new SimpleGrantedAuthority(role.getNombre())).collect(Collectors.toList());
		return new User(user.getEmail(),user.getPsw(),user.isEnabled(),true,true,true,authorities);
	}

	@Override
	@Transactional()
	public Usuario findByEmail(String email) {
		
		return repo.findByEmail(email);
	}

	@Override
	@Transactional
	public void deletebyid(int id) {
		repo.deleteById(id);
		
	}

}
