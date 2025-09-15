package com.thunderfat.springboot.backend.auth;

/**
 * Temporary security configuration for testing alimento endpoints
 * IMPORTANT: This is for development/testing only and should be removed in production
 * Only active when NOT in test profile to avoid conflicts with TestSecurityConfig
 * CURRENTLY DISABLED - using main SpringSecurityConfig instead
 */
//@EnableWebSecurity
//@RequiredArgsConstructor
//@Configuration(proxyBeanMethods = false)
//@Profile("!test") // Only active when NOT in test profile
public class TestingSecurityConfig {

    // All methods disabled - using SpringSecurityConfig instead
	
	/*
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
	*/
}
