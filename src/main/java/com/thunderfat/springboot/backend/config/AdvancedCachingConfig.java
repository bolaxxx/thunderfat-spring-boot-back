package com.thunderfat.springboot.backend.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Advanced caching configuration for the application.
 * Demonstrates Spring Boot 2025 caching best practices:
 * - Different TTL (Time To Live) for different cache regions
 * - JSON serialization for cache values
 * - Conditional caching based on environment
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Configuration
@EnableCaching
@Profile("!test")
@Slf4j
public class AdvancedCachingConfig {
    
    @Bean("redisCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        log.info("Configuring advanced caching strategy with Redis");
        
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))  // Default TTL: 30 minutes
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()));
        
        // Builder for custom configurations
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaultConfig);
        
        // Custom TTL for specific caches
        builder.withCacheConfiguration("platos-predeterminados",
                defaultConfig.entryTtl(Duration.ofHours(2)));  // 2 hours
        
        builder.withCacheConfiguration("platos-by-nutricionista",
                defaultConfig.entryTtl(Duration.ofMinutes(60)));  // 60 minutes
        
        builder.withCacheConfiguration("platos-by-nutricionista-list",
                defaultConfig.entryTtl(Duration.ofMinutes(15)));  // 15 minutes (shorter TTL for lists)
        
        log.info("Configured cache TTLs: default=30m, platos-predeterminados=2h, platos-by-nutricionista=60m, platos-by-nutricionista-list=15m");
        
        return builder.build();
    }
}
