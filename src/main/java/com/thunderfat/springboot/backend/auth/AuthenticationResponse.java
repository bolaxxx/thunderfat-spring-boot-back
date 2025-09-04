package com.thunderfat.springboot.backend.auth;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {
    
    private String accessToken;
    
    @Builder.Default
    private String tokenType = "Bearer";
    
    private long expiresIn;
    
    private int userId;
    
    private String email;
    
    private List<String> roles;
    
    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();
    
    private String refreshToken;
}
