package com.thunderfat.springboot.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time chat messaging.
 * Enables STOMP messaging protocol for patient-nutritionist communication.
 * 
 * Features:
 * - Real-time message delivery
 * - Private conversation channels
 * - Notification broadcasting
 * - Cross-origin support for frontend clients
 * 
 * @author ThunderFat Development Team
 * @since Spring Boot 3.5.4
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker for handling messages.
     * - /topic: for broadcasting to multiple subscribers
     * - /queue: for point-to-point messaging
     * - /app: for application-specific destinations
     */
    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Enable simple broker for topic and queue destinations
        config.enableSimpleBroker("/topic", "/queue");
        
        // Set application destination prefix
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for private messaging
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Register STOMP endpoints for WebSocket connections.
     * Supports both WebSocket and SockJS fallback.
     */
    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // WebSocket endpoint for chat connections
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("http://localhost:4200", "http://localhost:8100")
                .withSockJS(); // Enable SockJS fallback for browsers that don't support WebSocket
        
        // Alternative endpoint without SockJS for modern browsers
        registry.addEndpoint("/ws/chat-native")
                .setAllowedOriginPatterns("http://localhost:4200", "http://localhost:8100");
    }
}
