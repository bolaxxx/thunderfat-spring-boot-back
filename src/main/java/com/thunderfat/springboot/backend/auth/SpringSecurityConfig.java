package com.thunderfat.springboot.backend.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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

import com.thunderfat.springboot.backend.config.ThunderFatProperties;

import lombok.RequiredArgsConstructor;


@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@Profile("!test")  // Disable this configuration during tests
public class SpringSecurityConfig {
	private final JwtAuthenticationFilter jwtAutheFilter;
	private final ThunderFatProperties properties;

    @Bean
    @Order(2)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				// API v1 auth endpoints (public)
				.requestMatchers("/api/v1/auth/**").permitAll()
				// Legacy auth endpoints (for backward compatibility)
				.requestMatchers("/api/auth/**", "/api/public/**", "/api/info", "/api/endpoints", "/api/test", "/api/health/**", "/oauth2/**", "/.well-known/**", "/actuator/**", "/login.html").permitAll()
				// OpenAPI/SpringDoc endpoints
				.requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
				// All other requests require authentication
				.anyRequest().authenticated()
			)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.headers(headers -> {
				if (properties.getSecurity().isEnableSecurityHeaders()) {
					// HSTS (HTTP Strict Transport Security)
					headers.httpStrictTransportSecurity(hsts -> hsts
						.maxAgeInSeconds(31536000) // 1 year
						.includeSubDomains(true)
						.preload(true));
					
					// Prevent MIME type sniffing
					headers.contentTypeOptions(contentTypeOptions -> {});
					
					// Prevent page embedding in frames/iframes
					headers.frameOptions(frameOptions -> frameOptions.deny());
					
					// Content Security Policy
					headers.contentSecurityPolicy(csp -> csp
						.policyDirectives(properties.getSecurity().getContentSecurityPolicy()));
					
					// Additional security headers
					headers.addHeaderWriter((request, response) -> {
						response.setHeader("X-XSS-Protection", "1; mode=block");
						response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
						response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
						response.setHeader("Permissions-Policy", 
							"camera=(), microphone=(), geolocation=(), interest-cohort=()");
					});
				}
			})
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
				ThunderFatProperties.Cors corsConfig = properties.getCors();
				registry.addMapping("/**")
						.allowedMethods(corsConfig.getAllowedMethods().toArray(new String[0]))
						.allowedOriginPatterns(corsConfig.getAllowedOrigins().toArray(new String[0]))
						.allowedHeaders(corsConfig.getAllowedHeaders().toArray(new String[0]))
						.allowCredentials(corsConfig.isAllowCredentials())
						.maxAge(corsConfig.getMaxAge());
			}
		};
	}
}
