package com.assessment.security;

import com.assessment.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter - Intercepts every HTTP request
 * 
 * RESPONSIBILITIES:
 * 1. Extract JWT token from Authorization header
 * 2. Validate token signature and expiration
 * 3. Extract user email and role from token
 * 4. Set Spring Security authentication context
 * 
 * FLOW:
 * Request → Filter → Validate JWT → Set Auth Context → Controller
 * 
 * If token is valid:   Request proceeds to controller
 * If token is invalid: Returns 401 Unauthorized
 * If no token:         Public endpoints work, protected return 401
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Main filter method - called for every HTTP request.
     * 
     * STEPS:
     * 1. Extract Authorization header
     * 2. Check if it starts with "Bearer "
     * 3. Extract token (remove "Bearer " prefix)
     * 4. Validate token and extract email
     * 5. Set Spring Security authentication
     * 6. Continue filter chain
     * 
     * @param request - HTTP request
     * @param response - HTTP response
     * @param filterChain - continues to next filter/controller
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        // Get Authorization header
        String authHeader = request.getHeader("Authorization");
        
        // Check if header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            
            // Extract token (remove "Bearer " prefix)
            String token = authHeader.substring(7);
            
            try {
                // Extract email from token
                String email = jwtUtil.extractEmail(token);
                
                // Validate token with email
                if (email != null && jwtUtil.validateToken(token, email)) {
                    
                    // Extract role from token
                    String role = jwtUtil.extractRole(token);
                    
                    // Create authority (Spring Security requires "ROLE_" prefix)
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
                    
                    // Create authentication object
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            email,                                    // Principal (user identifier)
                            null,                                     // Credentials (not needed after authentication)
                            Collections.singletonList(authority)      // Authorities (roles)
                        );
                    
                    // Set authentication in Spring Security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                
            } catch (Exception e) {
                // Token validation failed - don't set authentication
                // Request will proceed but won't have authentication
                // Protected endpoints will return 401
            }
        }
        
        // Continue filter chain (proceed to controller)
        filterChain.doFilter(request, response);
    }
}
