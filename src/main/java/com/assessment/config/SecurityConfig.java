package com.assessment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
 * INTERVIEW TALKING POINT:
 * "BCrypt is a slow hashing function designed to resist brute-force attacks.
 * It automatically generates a random salt for each password, preventing
 * rainbow table attacks. The work factor can be adjusted as hardware improves."
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
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
     * Configure HTTP security to permit all endpoints (for now).
     * 
     * WHY PERMIT ALL?
     * - API #1 (generate) and #2 (batch) should be unsecured per requirements
     * - JWT authentication will be added later for API #3, #4, #5
     * 
     * LATER: We'll add JWT filter and restrict /api/users/me and /api/users/{username}
     * 
     * @param http - HttpSecurity configuration object
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for REST API
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()   // Allow all endpoints for now
            );
        
        return http.build();
    }
}
