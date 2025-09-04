package com.thunderfat.springboot.backend;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordVerificationRunner implements ApplicationRunner {
    
    @Override
    public void run(ApplicationArguments args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String storedHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        String plainPassword = "password";
        
        boolean matches = encoder.matches(plainPassword, storedHash);
        System.out.println("\n=================================================");
        System.out.println("PASSWORD VERIFICATION TEST");
        System.out.println("=================================================");
        System.out.println("Plain password: " + plainPassword);
        System.out.println("Stored hash: " + storedHash);
        System.out.println("Do they match? " + matches);
        System.out.println("=================================================\n");
    }
}
