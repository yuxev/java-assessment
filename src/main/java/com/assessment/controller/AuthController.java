package com.assessment.controller;

import com.assessment.dto.AuthResponse;
import com.assessment.dto.LoginRequest;
import com.assessment.model.User;
import com.assessment.repository.UserRepository;
import com.assessment.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Authentication endpoints.
 * 
 * ENDPOINT:
 * - POST /api/auth - Login with username/email + password, returns JWT
 */
@RestController
@RequestMapping("/api")
public class AuthController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public AuthController(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * ENDPOINT #3: Authentication - Login and generate JWT token
     * 
     * URL: POST http://localhost:9090/api/auth
     * 
     * REQUEST BODY:
     * {
     *   "username": "john.doe",  // Can be username OR email
     *   "password": "pass123"
     * }
     * 
     * RESPONSE (Success - 200):
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * }
     * 
     * RESPONSE (Failed - 401):
     * {
     *   "error": "Authentication failed",
     *   "message": "Invalid credentials"
     * }
     * 
     * AUTHENTICATION LOGIC:
     * 1. Find user by username OR email
     * 2. Verify password using BCrypt
     * 3. Generate JWT token with user's email
     * 4. Return token in response
     * 
     * @param loginRequest - username/email + password
     * @return ResponseEntity with JWT token or error
     */
    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Find user by username or email
            Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
            
            if (userOpt.isEmpty()) {
                // Try finding by email if username lookup failed
                userOpt = userRepository.findByEmail(loginRequest.getUsername());
            }
            
            // User not found
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Authentication failed", "Invalid credentials"));
            }
            
            User user = userOpt.get();
            
            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Authentication failed", "Invalid credentials"));
            }
            
            // Generate JWT token with email and role
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
            
            // Return success response
            return ResponseEntity.ok(new AuthResponse(token));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Authentication error", e.getMessage()));
        }
    }
    
    /**
     * Helper method to create error response.
     * 
     * @param error - error type
     * @param message - error message
     * @return Map with error details
     */
    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        return response;
    }
}
