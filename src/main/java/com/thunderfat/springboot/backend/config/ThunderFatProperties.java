package com.thunderfat.springboot.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

import java.util.List;

/**
 * Configuration properties for ThunderFat API settings
 * Binds custom application properties to strongly-typed configuration
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thunderfat")
public class ThunderFatProperties {

    private Api api = new Api();
    private Cors cors = new Cors();
    private Security security = new Security();

    @Data
    public static class Api {
        private String version = "v1";
        private String basePath = "/api/v1";
        private boolean enableVersioning = true;
        private boolean enableEtag = true;
        private boolean enableCompression = true;
    }

    @Data
    public static class Cors {
        private List<String> allowedOrigins = List.of("http://localhost:4200", "http://localhost:8100");
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
        private List<String> allowedHeaders = List.of("*");
        private boolean allowCredentials = true;
        private long maxAge = 3600;
    }

    @Data
    public static class Security {
        private boolean enableSecurityHeaders = true;
        private boolean enableHsts = true;
        private boolean enableContentTypeOptions = true;
        private boolean enableFrameOptions = true;
        private String contentSecurityPolicy = "default-src 'self'";
    }
}
