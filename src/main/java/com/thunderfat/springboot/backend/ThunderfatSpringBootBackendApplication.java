package com.thunderfat.springboot.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ThunderfatSpringBootBackendApplication implements CommandLineRunner {
@Autowired
private BCryptPasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ThunderfatSpringBootBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		String contrasena= "12345";
		for (int i=0 ;i<5; i++) {
			String codified = passwordEncoder.encode(contrasena);
			System.out.println(codified);
		}
	}

}
