package com.thunderfat.springboot.backend.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

/**
 * Temporary security configuration for testing alimento endpoints
 * IMPORTANT: This is for development/testing only and should be removed in production
 */
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class TestingSecurityConfig {
	private final JwtAuthenticationFilter jwtAutheFilter;

    @Bean
    @Primary
    @Order(1) // Higher priority than the original config
    SecurityFilterChain testingFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**", "/api/public/**", "/api/info", 
					"/api/endpoints", "/api/test", "/api/health/**", "/oauth2/**", 
					"/.well-known/**", "/actuator/**", "/login.html", 
					"/alimentos/**") // ADDED alimentos endpoints for testing
				.permitAll()
				.anyRequest().authenticated()
			)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(Customizer.withDefaults())
			)
			.addFilterBefore(jwtAutheFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}

    @Bean("testingCorsConfigurer")
    @Primary
    WebMvcConfigurer testingCorsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NonNull CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
						.allowedOriginPatterns("*") 
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
}
