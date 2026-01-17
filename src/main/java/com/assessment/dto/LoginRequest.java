package com.assessment.dto;

/**
 * DTO for login requests to /api/auth endpoint.
 * 
 * Accepts either username OR email in the username field.
 * Password is validated against BCrypt-encoded password in database.
 */
public class LoginRequest {
    
    private String username;  // Can be username OR email
    private String password;  // Plain text password from client
    
    public LoginRequest() {
    }
    
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
