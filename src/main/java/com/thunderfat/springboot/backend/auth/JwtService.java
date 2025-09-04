package com.thunderfat.springboot.backend.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

@Service
public class JwtService {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    
    @Value("${jwt.secret:MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0gs5K84BInHyfE8HZIn}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;
    
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    /**
     * Extracts the username (email) from the JWT token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extracts the expiration date from the JWT token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extracts user ID from the JWT token
     */
    public Integer extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id_usuario", Integer.class));
    }

    /**
     * Extracts a specific claim from the JWT token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Error extracting claim from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Generates a token with default claims for the user
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a token with additional claims for the user
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }
    
    /**
     * Generates a refresh token for the user
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");
        return buildToken(claims, userDetails, refreshExpiration);
    }

    /**
     * Builds the JWT token with the specified claims and expiration
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Validates if the token is valid for the given user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username != null && username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates if the token format is valid (without checking expiration or user)
     */
    public boolean isTokenFormatValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Invalid token format: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the token is expired
     */
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration != null && expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Error checking token expiration: {}", e.getMessage());
            return true; // Consider expired if we can't read the expiration
        }
    }

    /**
     * Extracts all claims from the JWT token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw new JwtException("Invalid JWT signature", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token: {}", e.getMessage());
            throw new JwtException("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
            throw new JwtException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT token compact of handler are invalid: {}", e.getMessage());
            throw new JwtException("JWT token compact of handler are invalid", e);
        }
    }

    /**
     * Gets the signing key for JWT tokens
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
