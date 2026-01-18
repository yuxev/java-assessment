package com.assessment.controller;

import com.assessment.dto.LoginRequest;
import com.assessment.model.User;
import com.assessment.repository.UserRepository;
import com.assessment.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController (JWT Authentication).
 * 
 * ENDPOINT: POST /api/auth
 * 
 * PURPOSE:
 * - User login with username/email + password
 * - Generate JWT token for authenticated users
 * - Return access token in JSON response
 * 
 * REQUIREMENTS: API #3 from objective.txt
 * 
 * @WebMvcTest - Lightweight test focusing only on web layer
 * @AutoConfigureMockMvc(addFilters = false) - Disables Spring Security during tests
 */
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
                classes = {com.assessment.security.JwtAuthenticationFilter.class}))
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private PasswordEncoder passwordEncoder;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    /**
     * Tests for POST /api/auth
     * 
     * This endpoint authenticates users and returns JWT tokens.
     * Requirements: API #3 from objective.txt
     */
    @Nested
    class LoginEndpointTests {
        
        @Test
        void shouldReturnJwtTokenForValidCredentialsWithUsername() throws Exception {
            // GIVEN: Valid user exists with correct password
            User testUser = createTestUser("john.doe", "john@example.com", "encodedPassword123", "user");
            LoginRequest loginRequest = new LoginRequest("john.doe", "plainPassword123");
            String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
            
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("plainPassword123", "encodedPassword123")).thenReturn(true);
            when(jwtUtil.generateToken("john@example.com", "user")).thenReturn(expectedToken);
            
            // WHEN: POST /api/auth with valid credentials
            mockMvc.perform(post("/api/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    
                    // THEN: Return 200 OK with JWT token
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value(expectedToken))
                    .andExpect(jsonPath("$.accessToken").isNotEmpty());
        }
        
        @Test
        void shouldAcceptEmailForLogin() throws Exception {
            // GIVEN: User can login with email instead of username
            User testUser = createTestUser("john.doe", "john@example.com", "encodedPassword123", "user");
            LoginRequest loginRequest = new LoginRequest("john@example.com", "plainPassword123");
            String expectedToken = "jwt.token.here";
            
            when(userRepository.findByUsername("john@example.com")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("plainPassword123", "encodedPassword123")).thenReturn(true);
            when(jwtUtil.generateToken("john@example.com", "user")).thenReturn(expectedToken);
            
            // WHEN: POST /api/auth with email
            mockMvc.perform(post("/api/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    
                    // THEN: Return 200 OK with JWT token
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value(expectedToken));
        }
        
        @Test
        void shouldReturn401ForInvalidPassword() throws Exception {
            // GIVEN: User exists but password is wrong
            User testUser = createTestUser("john.doe", "john@example.com", "encodedPassword123", "user");
            LoginRequest loginRequest = new LoginRequest("john.doe", "wrongPassword");
            
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);
            
            // WHEN: POST /api/auth with invalid password
            mockMvc.perform(post("/api/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    
                    // THEN: Return 401 Unauthorized
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Authentication failed"))
                    .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }
        
        @Test
        void shouldReturn401ForNonexistentUser() throws Exception {
            // GIVEN: User does not exist
            LoginRequest loginRequest = new LoginRequest("nonexistent", "password");
            
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("nonexistent")).thenReturn(Optional.empty());
            
            // WHEN: POST /api/auth with nonexistent user
            mockMvc.perform(post("/api/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    
                    // THEN: Return 401 Unauthorized (don't reveal user doesn't exist)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Authentication failed"))
                    .andExpect(jsonPath("$.message").value("Invalid credentials"));
        }
        
        @Test
        void shouldGenerateTokenWithUserEmailAndRole() throws Exception {
            // GIVEN: Admin user with specific email and role
            User adminUser = createTestUser("admin", "admin@example.com", "encodedPass", "admin");
            LoginRequest loginRequest = new LoginRequest("admin", "password");
            String expectedToken = "admin.jwt.token";
            
            when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
            when(passwordEncoder.matches("password", "encodedPass")).thenReturn(true);
            when(jwtUtil.generateToken("admin@example.com", "admin")).thenReturn(expectedToken);
            
            // WHEN: Login as admin
            mockMvc.perform(post("/api/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    
                    // THEN: JWT should be generated with admin email and role
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value(expectedToken));
        }
        
        @Test
        void shouldReturnErrorForMissingUsername() throws Exception {
            // GIVEN: LoginRequest with null username
            String invalidJson = "{\"password\":\"test123\"}";
            
            // WHEN: POST /api/auth without username
            mockMvc.perform(post("/api/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    
                    // THEN: Should handle gracefully (401 or 400)
                    .andExpect(status().is4xxClientError());
        }
        
        @Test
        void shouldReturnErrorForMissingPassword() throws Exception {
            // GIVEN: LoginRequest with null password
            String invalidJson = "{\"username\":\"john.doe\"}";
            
            when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
            
            // WHEN: POST /api/auth without password
            mockMvc.perform(post("/api/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    
                    // THEN: Should handle gracefully
                    .andExpect(status().is4xxClientError());
        }
    }
    
    /**
     * Helper method to create test user
     */
    private User createTestUser(String username, String email, String encodedPassword, String role) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }
}
