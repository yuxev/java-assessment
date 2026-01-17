package com.assessment.dto;

/**
 * DTO for authentication response.
 * 
 * Contains the JWT token to be used for subsequent secured requests.
 */
public class AuthResponse {
    
    private String accessToken;
    
    public AuthResponse() {
    }
    
    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
