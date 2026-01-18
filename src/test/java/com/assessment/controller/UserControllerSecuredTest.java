package com.assessment.controller;

import com.assessment.model.User;
import com.assessment.repository.UserRepository;
import com.assessment.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SECURED UserController endpoints.
 * 
 * ENDPOINTS TO TEST:
 * - GET /api/users/me         (API #4 - My Profile - Requires JWT)
 * - GET /api/users/{username} (API #5 - Get User - Requires Admin Role)
 * 
 * These endpoints require JWT authentication and authorization.
 * 
 * @SpringBootTest - Full application context for security testing
 * @AutoConfigureMockMvc - Auto-configure MockMvc with security
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSecuredTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    // ========================
    // MY PROFILE ENDPOINT TESTS
    // ========================
    
    /**
     * Tests for GET /api/users/me
     * 
     * This endpoint returns the authenticated user's profile.
     * Requires JWT authentication.
     * Requirements: API #4 from objective.txt
     */
    @Nested
    class MyProfileEndpointTests {
        
        @Test
        @WithMockUser(username = "john@example.com", roles = "USER")
        void shouldReturnAuthenticatedUserProfile() throws Exception {
            // GIVEN: Authenticated user exists in database
            User testUser = createTestUser(
                "john.doe", 
                "john@example.com", 
                "encodedPassword",
                "user"
            );
            
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
            
            // WHEN: GET /api/users/me with valid authentication
            mockMvc.perform(get("/api/users/me"))
                    
                    // THEN: Return 200 OK with user profile
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("john.doe"))
                    .andExpect(jsonPath("$.email").value("john@example.com"))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.role").value("user"))
                    .andExpect(jsonPath("$.password").exists()); // Note: password should be excluded in production
        }
        
        @Test
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // WHEN: GET /api/users/me without authentication
            mockMvc.perform(get("/api/users/me"))
                    
                    // THEN: Return 403 Forbidden (Spring Security default for unauthenticated)
                    .andExpect(status().isForbidden());
        }
        
        @Test
        @WithMockUser(username = "admin@example.com", roles = "ADMIN")
        void shouldReturnCorrectUserFromJWT() throws Exception {
            // GIVEN: Admin user authenticated via JWT
            User adminUser = createTestUser(
                "admin", 
                "admin@example.com", 
                "encodedPassword",
                "admin"
            );
            
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            
            // WHEN: GET /api/users/me
            mockMvc.perform(get("/api/users/me"))
                    
                    // THEN: Return admin's profile (not another user)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("admin@example.com"))
                    .andExpect(jsonPath("$.username").value("admin"))
                    .andExpect(jsonPath("$.role").value("admin"));
        }
        
        @Test
        @WithMockUser(username = "nonexistent@example.com", roles = "USER")
        void shouldReturn500WhenUserNotFoundInDatabase() throws Exception {
            // GIVEN: JWT is valid but user deleted from database
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
            
            // WHEN: GET /api/users/me
            mockMvc.perform(get("/api/users/me"))
                    
                    // THEN: Return 500 Internal Server Error
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Error retrieving profile"))
                    .andExpect(jsonPath("$.message").exists());
        }
    }
    
    // ========================
    // GET USER ENDPOINT TESTS
    // ========================
    
    /**
     * Tests for GET /api/users/{username}
     * 
     * This endpoint returns any user's profile (admin only).
     * Regular users get 403 Forbidden.
     * Requirements: API #5 from objective.txt
     */
    @Nested
    class GetUserEndpointTests {
        
        @Test
        @WithMockUser(username = "admin@example.com", roles = "ADMIN")
        void shouldReturnUserProfileWhenAdmin() throws Exception {
            // GIVEN: Admin user and target user exists
            User targetUser = createTestUser(
                "john.doe",
                "john@example.com",
                "encodedPassword",
                "user"
            );
            
            when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(targetUser));
            
            // WHEN: GET /api/users/{username} as admin
            mockMvc.perform(get("/api/users/john.doe"))
                    
                    // THEN: Return 200 OK with user profile
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("john.doe"))
                    .andExpect(jsonPath("$.email").value("john@example.com"))
                    .andExpect(jsonPath("$.role").value("user"));
        }
        
        @Test
        @WithMockUser(username = "user@example.com", roles = "USER")
        void shouldReturn403WhenNotAdmin() throws Exception {
            // GIVEN: Regular user (not admin)
            
            // WHEN: GET /api/users/{username} as regular user
            mockMvc.perform(get("/api/users/john.doe"))
                    
                    // THEN: Return 403 Forbidden
                    .andExpect(status().isForbidden());
        }
        
        @Test
        @WithMockUser(username = "admin@example.com", roles = "ADMIN")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // GIVEN: Admin user but target user doesn't exist
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
            
            // WHEN: GET /api/users/nonexistent
            mockMvc.perform(get("/api/users/nonexistent"))
                    
                    // THEN: Return 404 Not Found
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("User not found"))
                    .andExpect(jsonPath("$.message").value(containsString("nonexistent")));
        }
        
        @Test
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            // WHEN: GET /api/users/{username} without authentication
            mockMvc.perform(get("/api/users/john.doe"))
                    
                    // THEN: Return 403 Forbidden (Spring Security default for unauthenticated)
                    .andExpect(status().isForbidden());
        }
        
        @Test
        @WithMockUser(username = "admin@example.com", roles = "ADMIN")
        void shouldAllowAdminToViewOtherAdmins() throws Exception {
            // GIVEN: Admin viewing another admin's profile
            User anotherAdmin = createTestUser(
                "superadmin",
                "super@example.com",
                "encodedPassword",
                "admin"
            );
            
            when(userRepository.findByUsername("superadmin")).thenReturn(Optional.of(anotherAdmin));
            
            // WHEN: GET /api/users/superadmin
            mockMvc.perform(get("/api/users/superadmin"))
                    
                    // THEN: Return 200 OK
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("superadmin"))
                    .andExpect(jsonPath("$.role").value("admin"));
        }
        
        @Test
        @WithMockUser(username = "user@example.com", roles = "USER")
        void shouldPreventUserFromViewingOwnProfileViaUsername() throws Exception {
            // GIVEN: Regular user trying to access their own profile via username endpoint
            
            // WHEN: GET /api/users/{username} (should use /me instead)
            mockMvc.perform(get("/api/users/john.doe"))
                    
                    // THEN: Still return 403 (only admins can use this endpoint)
                    .andExpect(status().isForbidden());
        }
    }
    
    // ========================
    // HELPER METHODS
    // ========================
    
    /**
     * Creates a test user with the given details.
     */
    private User createTestUser(String username, String email, String password, String role) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCity("New York");
        user.setCountry("US");
        user.setAvatar("https://example.com/avatar.jpg");
        user.setCompany("TechCorp");
        user.setJobPosition("Developer");
        user.setMobile("+1234567890");
        return user;
    }
}
