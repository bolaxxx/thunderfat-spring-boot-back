package com.thunderfat.springboot.backend.config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

/**
 * Fallback caching configuration using Caffeine (in-memory) for development.
 * Only activates when Redis is not available or disabled via application properties.
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "caffeine", matchIfMissing = true)
@Slf4j
public class DevCachingConfig {

    @Bean
    public CacheManager caffeineManager() {
        log.info("Configuring development caching strategy with Caffeine");
        
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Default cache configuration
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES) // Default TTL: 30 minutes
                .maximumSize(1000));
        
        return cacheManager;
    }
}
