package com.thunderfat.springboot.backend.config;

import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.thunderfat.springboot.backend.auth.JwtService;

/**
 * Global test configuration that provides a simplified security setup for all test contexts.
 * Overrides the production security configuration with a permissive setup for testing.
 * This configuration is automatically imported by @WebMvcTest classes.
 */
@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false)
public class GlobalTestConfiguration {

    /**
     * Primary security filter chain that permits all requests during testing.
     * This overrides any other security configuration in the test context.
     */
    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/ws/**", "/app/**", "/topic/**", "/queue/**").permitAll()
                .requestMatchers("/api/auth/**", "/api/public/**", "/api/info", "/api/endpoints", "/api/test", "/api/health/**", "/oauth2/**", "/.well-known/**", "/actuator/**", "/login.html").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api-docs/**").permitAll()
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .oauth2ResourceServer(oauth2 -> oauth2.disable());

        return http.build();
    }

    /**
     * Test password encoder with lower strength for faster tests
     */
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    /**
     * Test user details service with mock users
     */
    @Bean
    @Primary
    public UserDetailsService testUserDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails nutricionista = User.builder()
                .username("test@nutricionista.com")
                .password(passwordEncoder.encode("password"))
                .roles("NUTRICIONISTA")
                .build();

        UserDetails admin = User.builder()
                .username("test@admin.com")
                .password(passwordEncoder.encode("password"))
                .roles("ADMIN")
                .build();

        UserDetails paciente = User.builder()
                .username("test@paciente.com")
                .password(passwordEncoder.encode("password"))
                .roles("PACIENTE")
                .build();

        return new InMemoryUserDetailsManager(nutricionista, admin, paciente);
    }

    /**
     * Test Authentication Manager - Using proper AuthenticationConfiguration
     * 
     * @param authConfig AuthenticationConfiguration to get AuthenticationManager from
     * @return AuthenticationManager configured for testing
     */
    @Bean
    @Primary
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * RestTemplate bean for test environment
     * This replaces the RestTemplate from the excluded ApplicationConfig
     * 
     * @return RestTemplate configured for testing
     */
    @Bean
    @Primary
    public org.springframework.web.client.RestTemplate restTemplate() {
        return new org.springframework.web.client.RestTemplate();
    }

    /**
     * Mock JwtService for test environment
     * This provides a mock JwtService for tests that require JWT functionality
     * 
     * @return Mock JwtService for testing
     */
    @Bean
    @Primary
    public JwtService jwtService() {
        return org.mockito.Mockito.mock(JwtService.class);
    }

}
