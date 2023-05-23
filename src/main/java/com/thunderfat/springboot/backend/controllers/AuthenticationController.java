package com.thunderfat.springboot.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.NoArgsConstructor;

@Controller
@NoArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    @PostMapping("/create")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createAuthenticationToken'");
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshAndGetAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshAndGetAuthenticationToken'");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerUser'");
    }

    
    
}
