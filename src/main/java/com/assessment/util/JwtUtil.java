package com.assessment.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for JWT token operations.
 * 
 * RESPONSIBILITIES:
 * - Generate JWT tokens with user email
 * - Validate JWT tokens
 * - Extract claims (email, expiration, etc.)
 * 
 * SECURITY:
 * - Uses HMAC-SHA256 algorithm
 * - Tokens expire after configured duration
 * - Secret key should be stored securely (application.properties)
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}")  // Default: 24 hours in milliseconds
    private long expirationMs;
    
    /**
     * Generate JWT token for authenticated user.
     * 
     * TOKEN STRUCTURE:
     * - Header: Algorithm (HS256) and type (JWT)
     * - Payload: email (subject), issued time, expiration time
     * - Signature: HMAC-SHA256 of header + payload + secret
     * 
     * @param email - user's email to embed in token
     * @param role - user's role (admin or user)
     * @return JWT token string
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    /**
     * Extract email from JWT token.
     * 
     * @param token - JWT token
     * @return email address
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    /**
     * Extract role from JWT token.
     * 
     * @param token - JWT token
     * @return role (admin or user)
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
    
    /**
     * Extract expiration date from JWT token.
     * 
     * @param token - JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
    
    /**
     * Check if token is expired.
     * 
     * @param token - JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Validate JWT token.
     * 
     * Checks:
     * 1. Token can be parsed (valid signature)
     * 2. Token is not expired
     * 3. Email matches expected email
     * 
     * @param token - JWT token
     * @param email - expected email from token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            return (tokenEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Extract all claims from JWT token.
     * 
     * @param token - JWT token
     * @return Claims object containing all token data
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    /**
     * Get signing key from secret.
     * 
     * @return Key for signing/verifying tokens
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
