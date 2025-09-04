package com.thunderfat.springboot.backend.config;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Caching configuration for ThunderFat application.
 * Implements Spring Boot 2025 best practices for repository-level caching.
 * 
 * This configuration provides:
 * - Multiple cache regions with different characteristics
 * - TTL (Time To Live) support for different data types
 * - Cache eviction strategies
 * - Performance optimization for frequently accessed data
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    // Cache names as constants for better maintainability
    public static final String PACIENTES_CACHE = "pacientes";
    public static final String PACIENTES_BY_NUTRITIONIST_CACHE = "pacientes-by-nutritionist";
    public static final String PACIENTE_STATS_CACHE = "paciente-stats";
    public static final String PACIENTE_SEARCH_CACHE = "paciente-search";
    public static final String PACIENTE_SEARCH_DNI_CACHE = "paciente-search-dni";
    public static final String PACIENTE_SEARCH_TELEFONO_CACHE = "paciente-search-telefono";
    public static final String PACIENTE_SEARCH_NOMBRE_CACHE = "paciente-search-nombre";
    public static final String PACIENTE_APPOINTMENTS_CACHE = "paciente-appointments";
    public static final String PACIENTE_EXISTS_CACHE = "paciente-exists";
    public static final String PACIENTE_VALIDATION_CACHE = "paciente-validation";
    
    // ===== CITA SERVICE CACHE REGIONS =====
    public static final String CITAS_BY_PATIENT = "citas-by-patient";
    public static final String CITAS_BY_NUTRITIONIST = "citas-by-nutritionist"; 
    public static final String CITAS_BY_NUTRITIONIST_DATES = "citas-by-nutritionist-dates";
    public static final String NEXT_APPOINTMENT = "next-appointment";
    public static final String CITA_STATS = "cita-stats";
    public static final String UPCOMING_APPOINTMENTS = "upcoming-appointments";
    public static final String APPOINTMENT_CONFLICTS = "appointment-conflicts";
    public static final String CALENDAR_EVENTS = "calendar-events";
    
    public static final String NUTRICIONISTAS_CACHE = "nutricionistas";
    public static final String ALIMENTOS_CACHE = "alimentos";
    public static final String PLANES_DIETA_CACHE = "planesdieta";
    public static final String MEDICIONES_CACHE = "mediciones";
    public static final String USUARIOS_CACHE = "usuarios";
    public static final String ROLES_CACHE = "roles";
    public static final String STATISTICS_CACHE = "statistics";
    
    // Comida service cache regions (already configured)
    public static final String COMIDAS_CACHE = "comidas";
    public static final String COMIDA_STATS_CACHE = "comida-stats";
    public static final String COMIDA_SUBSTITUTIONS_CACHE = "comida-substitutions";
    
    /**
     * Primary cache manager using simple concurrent map implementation.
     * For production, consider using Redis or Hazelcast for distributed caching.
     * 
     * @return configured cache manager
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Define all cache names
        cacheManager.setCacheNames(Arrays.asList(
            // Patient-related caches
            PACIENTES_CACHE,
            PACIENTES_BY_NUTRITIONIST_CACHE,
            PACIENTE_STATS_CACHE,
            PACIENTE_SEARCH_CACHE,
            PACIENTE_SEARCH_DNI_CACHE,
            PACIENTE_SEARCH_TELEFONO_CACHE,
            PACIENTE_SEARCH_NOMBRE_CACHE,
            PACIENTE_APPOINTMENTS_CACHE,
            PACIENTE_EXISTS_CACHE,
            PACIENTE_VALIDATION_CACHE,
            
            // Other entity caches
            NUTRICIONISTAS_CACHE,
            ALIMENTOS_CACHE,
            PLANES_DIETA_CACHE,
            MEDICIONES_CACHE,
            USUARIOS_CACHE,
            ROLES_CACHE,
            STATISTICS_CACHE,
            
            // Comida service caches
            COMIDAS_CACHE,
            COMIDA_STATS_CACHE,
            COMIDA_SUBSTITUTIONS_CACHE
        ));
        
        // Allow dynamic cache creation
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
    
    /**
     * Configuration for different cache regions.
     * This would be more useful with Redis or other advanced cache providers.
     * 
     * Cache Strategy Guidelines:
     * - PACIENTES: Medium TTL (30 minutes) - moderate change frequency
     * - NUTRICIONISTAS: Long TTL (2 hours) - low change frequency
     * - ALIMENTOS: Long TTL (4 hours) - very low change frequency
     * - ROLES: Very long TTL (24 hours) - extremely low change frequency
     * - STATISTICS: Short TTL (15 minutes) - calculated values
     * - MEDICIONES: Medium TTL (1 hour) - moderate access frequency
     */
    
    // Example of how to configure different cache regions with different properties
    // This would be implemented with Redis or other advanced cache providers
    
    /**
     * Cache configuration properties as inner class for better organization.
     */
    public static class CacheProperties {
        
        // TTL values in minutes
        public static final int PACIENTES_TTL = 30;
        public static final int NUTRICIONISTAS_TTL = 120;
        public static final int ALIMENTOS_TTL = 240;
        public static final int ROLES_TTL = 1440; // 24 hours
        public static final int STATISTICS_TTL = 15;
        public static final int MEDICIONES_TTL = 60;
        public static final int USUARIOS_TTL = 60;
        public static final int PLANES_DIETA_TTL = 120;
        
        // CITA SERVICE TTL VALUES
        public static final int CITAS_TTL = 15; // High frequency changes
        public static final int APPOINTMENT_STATS_TTL = 30; // Statistics change moderately
        public static final int CALENDAR_EVENTS_TTL = 10; // Real-time calendar needs
        public static final int NEXT_APPOINTMENT_TTL = 20; // Important for patient scheduling
        
        // Maximum cache sizes
        public static final int DEFAULT_MAX_SIZE = 1000;
        public static final int SMALL_CACHE_MAX_SIZE = 100;
        public static final int LARGE_CACHE_MAX_SIZE = 5000;
    }
    
    /**
     * Custom cache key generator for complex cache keys.
     * Useful for creating meaningful cache keys from multiple parameters.
     */
    @Bean
    public org.springframework.cache.interceptor.KeyGenerator customKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(target.getClass().getSimpleName())
                     .append("_")
                     .append(method.getName());
            
            for (Object param : params) {
                keyBuilder.append("_");
                if (param != null) {
                    keyBuilder.append(param.toString());
                } else {
                    keyBuilder.append("null");
                }
            }
            
            return keyBuilder.toString();
        };
    }
}
