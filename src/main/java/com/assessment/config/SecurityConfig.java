package com.assessment.config;

import com.assessment.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security configuration for password encryption and endpoint access.
 * 
 * WHY BCRYPT?
 * - Industry-standard password hashing algorithm
 * - Automatically handles "salt" (random data added to password)
 * - Adaptive: can increase complexity as computers get faster
 * - One-way hash: cannot decrypt back to original password
 * 
 * NEVER STORE PLAIN TEXT PASSWORDS!
 * Plain text: "password123" → stored as "password123" ❌ DANGEROUS!
 * BCrypt:     "password123" → stored as "$2a$10$N9qo8..." ✅ SECURE!
 * 
 * JWT STATELESS AUTHENTICATION:
 * - No sessions stored on server
 * - Each request includes JWT token in Authorization header
 * - Server validates token signature and expiration
 * 
 * INTERVIEW TALKING POINT:
 * "BCrypt is a slow hashing function designed to resist brute-force attacks.
 * JWT provides stateless authentication - the server doesn't need to store
 * sessions, making it scalable for microservices and distributed systems."
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize annotations
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    /**
     * Creates a BCryptPasswordEncoder bean for password hashing.
     * 
     * USAGE:
     * - Encoding: passwordEncoder.encode("password123") → "$2a$10$..."
     * - Matching:  passwordEncoder.matches("password123", "$2a$10$...") → true
     * 
     * @return PasswordEncoder instance (BCrypt implementation)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Configure HTTP security with JWT authentication.
     * 
     * ENDPOINT SECURITY:
     * - /api/users/generate - PUBLIC (no auth required)
     * - /api/users/batch    - PUBLIC (no auth required)
     * - /api/auth           - PUBLIC (login endpoint)
     * - /swagger-ui/**      - PUBLIC (Swagger UI pages)
     * - /v3/api-docs/**     - PUBLIC (OpenAPI documentation)
     * - /api/users/me       - SECURED (requires JWT)
     * - /api/users/{username} - SECURED (requires JWT + admin role)
     * 
     * SESSION POLICY:
     * - STATELESS: No HTTP sessions, each request must include JWT
     * - Server doesn't store session state
     * - Scalable for distributed systems
     * 
     * @param http - HttpSecurity configuration object
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Enable CORS
            .csrf(csrf -> csrf.disable())  // Disable CSRF for REST API (using JWT instead)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // No sessions, JWT only
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/users/generate", 
                    "/api/users/batch", 
                    "/api/auth",
                    "/swagger-ui/**",      // Swagger UI interface
                    "/v3/api-docs/**"      // OpenAPI JSON documentation
                ).permitAll()
                .anyRequest().authenticated()  // All other endpoints require authentication
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter
        
        return http.build();
    }
    
    /**
     * CORS configuration to allow Swagger UI to make requests.
     * Allows requests from any origin (needed for Codespaces tunnel URLs).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));  // Allow all origins (for development)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);  // Must be false when allowedOrigins is *
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
