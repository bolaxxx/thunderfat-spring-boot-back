package com.thunderfat.springboot.backend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to disable OAuth2 Authorization Server during testing.
 * 
 * The authorization server is not needed for most tests and causes bean conflicts
 * with test security configurations.
 */
@Configuration
@ConditionalOnProperty(
    name = "thunderfat.oauth2.authorization-server.enabled", 
    havingValue = "true", 
    matchIfMissing = true
)
public class OAuth2AuthorizationServerToggle {
    // This configuration class exists only to provide conditional loading
    // When thunderfat.oauth2.authorization-server.enabled=false, 
    // the authorization server will not be loaded
}
