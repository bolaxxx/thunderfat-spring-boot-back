package com.thunderfat.springboot.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ThunderfatSpringBootBackendApplication implements CommandLineRunner {
	
	@Autowired(required = false)
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ThunderfatSpringBootBackendApplication.class, args);
	}

	@Override
	@Profile("!test")  // Don't run this in test environment
	public void run(String... args) throws Exception {
		// Only run if PasswordEncoder is available (not in test mode)
		if (passwordEncoder != null) {
			// Generar contraseñas codificadas para pruebas
			// Puedes eliminar este bloque una vez que hayas generado las contraseñas necesarias
			// y las hayas almacenado en tu base de datos o archivo de configuración.
			String contrasena= "12345";
			for (int i=0 ;i<5; i++) {
				String codified = passwordEncoder.encode(contrasena);
				System.out.println(codified);
			}
		}
	}
}
