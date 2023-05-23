package com.thunderfat.springboot.backend.model.service;

import org.springframework.stereotype.Service;

import com.thunderfat.springboot.backend.model.dao.UserRepository;
import com.thunderfat.springboot.backend.model.entity.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationResponse {
    private final UserRepository userRepository;
     
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user= Usuario.builder()
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'authenticate'");
    }
}
