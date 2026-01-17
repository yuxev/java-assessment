package com.assessment.controller;

import com.assessment.dto.BatchImportSummary;
import com.assessment.dto.GeneratedUser;
import com.assessment.model.User;
import com.assessment.repository.UserRepository;
import com.assessment.service.UserBatchService;
import com.assessment.service.UserGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for User endpoints.
 * 
 * @RestController = @Controller + @ResponseBody
 * Means: All methods return JSON automatically (not HTML views)
 * 
 * @RequestMapping - sets base path for all endpoints in this controller
 */

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserGenerationService userGenerationService;
    private final UserBatchService userBatchService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * Constructor injection of dependencies.
     * 
     * ObjectMapper is auto-configured by Spring Boot for JSON operations.
     */
    public UserController(UserGenerationService userGenerationService,
                         UserBatchService userBatchService,
                         UserRepository userRepository,
                         ObjectMapper objectMapper) {
        this.userGenerationService = userGenerationService;
        this.userBatchService = userBatchService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }
    
    /**
     * ENDPOINT #1: Generate Users - Downloads JSON file with fake users
     * 
     * URL: GET http://localhost:9090/api/users/generate?count=100
     * 
     * RESPONSE HEADERS:
     * - Content-Type: application/json
     * - Content-Disposition: attachment; filename="users-2026-01-15-14-30-45.json"
     *   This header triggers browser download instead of displaying JSON
     * 
     * ERROR HANDLING:
     * - Returns 400 Bad Request if count is invalid (< 1 or > 500)
     * - Returns 500 Internal Server Error if serialization fails
     * 
     * @param count - number of users to generate (default: 10, max: 500)
     * @return ResponseEntity with JSON file as downloadable attachment
     */
    @GetMapping("/generate")
    public ResponseEntity<?> generateUsers(@RequestParam(defaultValue = "10") int count) {
        try {
            // Call service to generate users
            List<GeneratedUser> users = userGenerationService.generateUsers(count);
            
            // Serialize list to JSON bytes
            byte[] jsonBytes = objectMapper.writeValueAsBytes(users);
            
            // Create filename with timestamp
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
            String filename = "users-" + timestamp + ".json";
            
            // Build response with download headers
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonBytes);
                
        } catch (IllegalArgumentException e) {
            // Validation error (count out of range)
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid request");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            
        } catch (Exception e) {
            // Unexpected error (serialization, etc.)
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", "Failed to generate users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * ENDPOINT #2: Batch Upload - Import users from JSON file
     * 
     * URL: POST http://localhost:9090/api/users/batch
     * Content-Type: multipart/form-data
     * Body: file (JSON file generated from API #1)
     * 
     * PROCESS:
     * 1. Receive uploaded file
     * 2. Parse JSON to List<GeneratedUser>
     * 3. Service checks duplicates and encrypts passwords
     * 4. Save to database
     * 5. Return summary JSON
     * 
     * RESPONSE EXAMPLE:
     * {
     *   "total": 100,
     *   "imported": 95,
     *   "rejected": 5
     * }
     * 
     * ERROR HANDLING:
     * - Returns 400 if file is missing or invalid JSON
     * - Returns 500 if database error occurs
     * 
     * INTERVIEW TALKING POINTS:
     * - "@RequestParam MultipartFile handles file uploads in Spring"
     * - "ObjectMapper.readValue() parses JSON bytes to Java objects"
     * - "Service layer handles duplicate detection and password encryption"
     * - "@Transactional ensures atomic database operations"
     * 
     * @param file - uploaded JSON file (from API #1)
     * @return BatchImportSummary with import statistics
     */
    @PostMapping("/batch")
    public ResponseEntity<?> batchUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file presence
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid request");
                error.put("message", "File is required and cannot be empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Parse JSON file to List<GeneratedUser>
            // ObjectMapper.readValue converts JSON bytes → Java objects
            List<GeneratedUser> users = objectMapper.readValue(
                file.getBytes(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, GeneratedUser.class)
            );
            
            // Call service to import users (dedupe, encrypt, save)
            BatchImportSummary summary = userBatchService.importUsers(users);
            
            // Return success response with summary
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            // Handle JSON parsing errors, database errors, etc.
            Map<String, String> error = new HashMap<>();
            error.put("error", "Import failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * ENDPOINT #4: Get My Profile - Returns authenticated user's profile
     * 
     * URL: GET http://localhost:9090/api/users/me
     * Headers: Authorization: Bearer <jwt-token>
     * 
     * AUTHENTICATION:
     * - Requires valid JWT token in Authorization header
     * - JWT filter extracts user email and sets authentication
     * - This method receives authentication from Spring Security context
     * 
     * PROCESS:
     * 1. Authentication object contains user's email (from JWT)
     * 2. Find user by email in database
     * 3. Return user profile (excluding password)
     * 
     * RESPONSE (Success - 200):
     * {
     *   "id": 1,
     *   "username": "john.doe",
     *   "email": "john@example.com",
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "role": "user",
     *   ...
     * }
     * 
     * ERROR HANDLING:
     * - Returns 401 if no JWT token or invalid token
     * - Returns 404 if user not found in database
     * 
     * @param authentication - Spring Security authentication object (auto-injected)
     * @return User profile data
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            // Authentication is null if no valid JWT token
            if (authentication == null || !authentication.isAuthenticated()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized");
                error.put("message", "Valid JWT token required");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            // Get email from authentication (set by JWT filter)
            String email = authentication.getName();
            
            // Find user by email
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Return user profile
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error retrieving profile");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * ENDPOINT #5: Get User by Username - Returns any user's profile (ADMIN ONLY)
     * 
     * URL: GET http://localhost:9090/api/users/{username}
     * Headers: Authorization: Bearer <jwt-token>
     * 
     * AUTHORIZATION:
     * - Requires valid JWT token with ADMIN role
     * - @PreAuthorize checks role before method execution
     * - Regular users get 403 Forbidden
     * 
     * PROCESS:
     * 1. JWT filter validates token and extracts role
     * 2. Spring Security checks if role is ADMIN
     * 3. If authorized, find user by username
     * 4. Return user profile
     * 
     * RESPONSE (Success - 200):
     * {
     *   "id": 1,
     *   "username": "john.doe",
     *   "email": "john@example.com",
     *   "role": "user",
     *   ...
     * }
     * 
     * ERROR HANDLING:
     * - Returns 401 if no JWT token or invalid token
     * - Returns 403 if user role is not ADMIN
     * - Returns 404 if username not found
     * 
     * @param username - username to look up
     * @return User profile data
     */
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            // Find user by username
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            // Return user profile
            return ResponseEntity.ok(user);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error retrieving user");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
