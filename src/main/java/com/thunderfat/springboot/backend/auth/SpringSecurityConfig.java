package com.thunderfat.springboot.backend.auth;

import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration; // Temporarily disabled
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


@EnableWebSecurity
@RequiredArgsConstructor
//@Configuration  // Temporarily disabled while TestingSecurityConfig is active
public class SpringSecurityConfig {
	private final JwtAuthenticationFilter jwtAutheFilter;

    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**", "/api/public/**", "/api/info", "/api/endpoints", "/api/test", "/api/health/**", "/oauth2/**", "/.well-known/**", "/actuator/**", "/login.html").permitAll()
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

    @Bean
    WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NonNull CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
						.allowedOriginPatterns("*") // Use allowedOriginPatterns instead of allowedOrigins for "*"
						.allowedHeaders("*")
						.allowCredentials(true); // Enable credentials for secure CORS
			}
		};
	}
}
