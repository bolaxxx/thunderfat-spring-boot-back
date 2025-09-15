package com.thunderfat.springboot.backend.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis Cache Configuration for ThunderFat Application.
 * 
 * This configuration provides advanced Redis caching with:
 * - Multiple cache regions with different TTL settings
 * - JSON serialization for complex objects
 * - Optimized cache configurations per service
 * - Fallback support when Redis is unavailable
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.5
 */
@Configuration
@Profile("!test")
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = false)
public class RedisConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    
    /**
     * Redis-based cache manager with custom TTL settings per cache region.
     * Replaces the simple cache manager when Redis is available.
     * 
     * @param connectionFactory Redis connection factory
     * @return configured Redis cache manager
     */
    @Bean
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        logger.info("Configuring advanced caching strategy with Redis");
        
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)) // Default 30 minutes TTL
            .disableCachingNullValues()
            .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        // Custom TTL configurations for different cache regions
        Map<String, RedisCacheConfiguration> cacheConfigurations = createCacheConfigurations(defaultConfig);
        
        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .transactionAware() // Support for @Transactional
            .build();
        
        // Log cache configuration summary
        logCacheConfiguration(cacheConfigurations);
        
        return cacheManager;
    }
    
    /**
     * RedisTemplate for manual Redis operations.
     * 
     * @param connectionFactory Redis connection factory
     * @return configured RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Use JSON serializer for values
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.setDefaultSerializer(jsonSerializer);
        template.afterPropertiesSet();
        
        return template;
    }
    
    /**
     * Creates cache configurations with custom TTL settings for different cache regions.
     * 
     * @param defaultConfig base configuration to customize
     * @return map of cache names to their configurations
     */
    private Map<String, RedisCacheConfiguration> createCacheConfigurations(RedisCacheConfiguration defaultConfig) {
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        
        // ===== PATIENT-RELATED CACHES =====
        configs.put(CacheConfig.PACIENTES_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configs.put(CacheConfig.PACIENTES_BY_NUTRITIONIST_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(45)));
        configs.put(CacheConfig.PACIENTE_STATS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        configs.put(CacheConfig.PACIENTE_SEARCH_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(10)));
        configs.put(CacheConfig.PACIENTE_SEARCH_DNI_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(60)));
        configs.put(CacheConfig.PACIENTE_SEARCH_TELEFONO_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(60)));
        configs.put(CacheConfig.PACIENTE_SEARCH_NOMBRE_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configs.put(CacheConfig.PACIENTE_APPOINTMENTS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        configs.put(CacheConfig.PACIENTE_EXISTS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(60)));
        configs.put(CacheConfig.PACIENTE_VALIDATION_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // ===== CITA SERVICE CACHES =====
        configs.put(CacheConfig.CITAS_BY_PATIENT, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        configs.put(CacheConfig.CITAS_BY_NUTRITIONIST, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        configs.put(CacheConfig.CITAS_BY_NUTRITIONIST_DATES, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        configs.put(CacheConfig.NEXT_APPOINTMENT, defaultConfig.entryTtl(Duration.ofMinutes(20)));
        configs.put(CacheConfig.CITA_STATS, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configs.put(CacheConfig.UPCOMING_APPOINTMENTS, defaultConfig.entryTtl(Duration.ofMinutes(10)));
        configs.put(CacheConfig.APPOINTMENT_CONFLICTS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configs.put(CacheConfig.CALENDAR_EVENTS, defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // ===== CORE ENTITY CACHES =====
        configs.put(CacheConfig.NUTRICIONISTAS_CACHE, defaultConfig.entryTtl(Duration.ofHours(2)));
        configs.put(CacheConfig.ALIMENTOS_CACHE, defaultConfig.entryTtl(Duration.ofHours(4)));
        configs.put(CacheConfig.PLANES_DIETA_CACHE, defaultConfig.entryTtl(Duration.ofHours(2)));
        configs.put(CacheConfig.MEDICIONES_CACHE, defaultConfig.entryTtl(Duration.ofHours(1)));
        configs.put(CacheConfig.USUARIOS_CACHE, defaultConfig.entryTtl(Duration.ofHours(1)));
        configs.put(CacheConfig.ROLES_CACHE, defaultConfig.entryTtl(Duration.ofHours(24)));
        configs.put(CacheConfig.STATISTICS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        
        // ===== COMIDA SERVICE CACHES =====
        configs.put(CacheConfig.COMIDAS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configs.put(CacheConfig.COMIDA_STATS_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configs.put(CacheConfig.COMIDA_SUBSTITUTIONS_CACHE, defaultConfig.entryTtl(Duration.ofHours(2)));
        
        // ===== PLATO PREDETERMINADO CACHES =====
        configs.put(CacheConfig.PLATOS_PREDETERMINADOS_CACHE, defaultConfig.entryTtl(Duration.ofHours(2)));
        configs.put(CacheConfig.PLATOS_BY_NUTRICIONISTA_CACHE, defaultConfig.entryTtl(Duration.ofHours(1)));
        configs.put(CacheConfig.PLATOS_BY_NUTRICIONISTA_LIST_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        
        // ===== SPANISH BILLING CACHES =====
        configs.put("facturas", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configs.put("facturas-stats", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        configs.put("facturas-paginated", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        configs.put("facturas-paciente", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configs.put("iva-calculations", defaultConfig.entryTtl(Duration.ofHours(4)));
        configs.put("verifactu-responses", defaultConfig.entryTtl(Duration.ofHours(24)));
        configs.put("certificados-cache", defaultConfig.entryTtl(Duration.ofHours(12)));
        
        return configs;
    }
    
    /**
     * Logs the cache configuration summary for debugging purposes.
     * 
     * @param cacheConfigurations map of cache configurations
     */
    private void logCacheConfiguration(Map<String, RedisCacheConfiguration> cacheConfigurations) {
        logger.info("Configured cache TTLs: default=30m, platos-predeterminados=2h, platos-by-nutricionista=60m, platos-by-nutricionista-list=15m");
        
        if (logger.isDebugEnabled()) {
            logger.debug("Redis cache regions configured:");
            cacheConfigurations.forEach((name, config) -> {
                logger.debug("  {} -> Custom TTL configured", name);
            });
        }
    }
}
