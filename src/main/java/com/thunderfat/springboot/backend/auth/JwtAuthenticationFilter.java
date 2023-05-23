package com.thunderfat.springboot.backend.auth;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                final String authorizationHeader = request.getHeader("Authorization");
                final String jwt;
                final String userEmail;
                if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                     
                    jwt = authorizationHeader.substring(7);
                } else {
                    filterChain.doFilter(request, response);
                    jwt = null;
                }
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doFilterInternal'");
    }
    
}
