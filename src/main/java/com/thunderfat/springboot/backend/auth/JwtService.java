package com.thunderfat.springboot.backend.auth;

import java.security.Key;
import java.util.Map;
import java.util.function.Function;

import org.apache.naming.java.javaURLContextFactory;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    public static JwtConfig jwtConfig;
    public String extractUsername(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractUsernamer'");
    }
    public<T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        // TODO Auto-generated method stub
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(Map<String,Object>extraClaims ,UserD) {
        // TODO Auto-generated method stub
        return createToken(username);
    }
    private Claims extractAllClaims(String token) {
        // TODO Auto-generated method stub
     return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody( );
        
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JwtConfig.RSA_PRIVADA);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
}
