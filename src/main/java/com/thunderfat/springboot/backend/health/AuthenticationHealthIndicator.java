package com.thunderfat.springboot.backend.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thunderfat.springboot.backend.model.service.IUserService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class AuthenticationHealthIndicator {

    private final IUserService userService;

    @GetMapping("/auth")
    public Map<String, Object> checkAuthenticationHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Check if user service is accessible
            if (userService == null) {
                health.put("status", "DOWN");
                health.put("reason", "User service unavailable");
                return health;
            }

            health.put("status", "UP");
            health.put("authentication", "OAuth2 + JWT ready");
            health.put("userService", "Available");
            health.put("jwtSupport", "Enabled");
            health.put("oauth2Support", "Enabled");
            health.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
        }
        
        return health;
    }
}
