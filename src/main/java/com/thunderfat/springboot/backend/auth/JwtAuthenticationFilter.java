package com.thunderfat.springboot.backend.auth;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            String jwt = extractJwtFromRequest(request);
            
            if (jwt == null) {
                logger.debug("No JWT token found in request to {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }
            
            String userEmail = jwtService.extractUsername(jwt);
            
            if (!StringUtils.hasText(userEmail)) {
                logger.warn("Unable to extract username from JWT token");
                filterChain.doFilter(request, response);
                return;
            }
            
            // If user is already authenticated, skip
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.debug("User {} already authenticated", userEmail);
                filterChain.doFilter(request, response);
                return;
            }
            
            authenticateUser(request, jwt, userEmail);
            
        } catch (JwtException e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            // Continue with filter chain - let Spring Security handle unauthenticated request
        } catch (Exception e) {
            logger.error("Unexpected error in JWT authentication filter", e);
            // Continue with filter chain to avoid blocking the request
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from the Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    /**
     * Authenticates the user based on the JWT token
     */
    private void authenticateUser(HttpServletRequest request, String jwt, String userEmail) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                logger.debug("Successfully authenticated user: {}", userEmail);
            } else {
                logger.warn("Invalid JWT token for user: {}", userEmail);
            }
        } catch (UsernameNotFoundException e) {
            logger.warn("User not found for JWT token: {}", userEmail);
        } catch (Exception e) {
            logger.error("Error authenticating user from JWT token", e);
        }
    }

    /**
     * Skip JWT authentication for certain paths
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        return path.startsWith("/api/auth/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/static/");
    }
}
