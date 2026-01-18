package com.assessment.controller;

import com.assessment.dto.BatchImportSummary;
import com.assessment.dto.GeneratedUser;
import com.assessment.service.UserBatchService;
import com.assessment.service.UserGenerationService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UNSECURED UserController endpoints.
 * 
 * ENDPOINTS TESTED:
 * - GET  /api/users/generate (API #1 - User Generation)
 * - POST /api/users/batch    (API #2 - Batch Upload)
 * 
 * These endpoints are publicly accessible without authentication.
 * 
 * @WebMvcTest - Lightweight test focusing only on web layer
 * @AutoConfigureMockMvc(addFilters = false) - Disables Spring Security filters for testing
 */
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = UserController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
                classes = {com.assessment.security.JwtAuthenticationFilter.class}))
class UserControllerUnsecuredTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserGenerationService userGenerationService;
    
    @MockBean
    private UserBatchService userBatchService;
    
    @MockBean
    private com.assessment.repository.UserRepository userRepository;
    
    // ========================
    // GENERATE ENDPOINT TESTS
    // ========================
    
    /**
     * Tests for GET /api/users/generate
     * 
     * This endpoint generates fake user data and downloads as JSON file.
     * Requirements: API #1 from objective.txt
     */
    @Nested
    class GenerateEndpointTests {
        
        @Test
        void shouldReturnJsonFile() throws Exception {
            // GIVEN: Service returns 5 fake users
            List<GeneratedUser> mockUsers = createMockUsers(5);
            when(userGenerationService.generateUsers(5)).thenReturn(mockUsers);
            
            // WHEN: Call GET /api/users/generate?count=5
            mockMvc.perform(get("/api/users/generate")
                    .param("count", "5"))
                    
                    // THEN: Verify response
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(header().string("Content-Disposition", 
                        matchesPattern("attachment; filename=\"users-\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2}-\\d{2}\\.json\"")))
                    .andExpect(jsonPath("$", hasSize(5)))
                    .andExpect(jsonPath("$[0].firstName").exists())
                    .andExpect(jsonPath("$[0].email").exists());
        }
        
        @Test
        void shouldUseDefaultCount() throws Exception {
            // GIVEN: Service returns 10 users (default)
            List<GeneratedUser> mockUsers = createMockUsers(10);
            when(userGenerationService.generateUsers(10)).thenReturn(mockUsers);
            
            // WHEN: Call without count parameter
            mockMvc.perform(get("/api/users/generate"))
                    
                    // THEN: Uses default count of 10
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(10)));
        }
        
        @Test
        void shouldRejectInvalidCount() throws Exception {
            // GIVEN: Service throws exception for count = 0
            when(userGenerationService.generateUsers(0))
                .thenThrow(new IllegalArgumentException("Count must be between 1 and 500"));
            
            // WHEN: Call with invalid count
            mockMvc.perform(get("/api/users/generate")
                    .param("count", "0"))
                    
                    // THEN: Returns 400 Bad Request
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid request"))
                    .andExpect(jsonPath("$.message").value(containsString("must be between 1 and 500")));
        }
    }
    
    // ========================
    // BATCH UPLOAD ENDPOINT TESTS
    // ========================
    
    /**
     * Tests for POST /api/users/batch
     * 
     * This endpoint uploads JSON files and imports users to database.
     * Handles duplicate detection and password encryption.
     * Requirements: API #2 from objective.txt
     */
    @Nested
    class BatchUploadEndpointTests {
        
        @Test
        void shouldImportValidFile() throws Exception {
            // GIVEN: Create fake JSON file with 3 users
            List<GeneratedUser> users = createMockUsers(3);
            String jsonContent = objectMapper.writeValueAsString(users);
            
            MockMultipartFile file = new MockMultipartFile(
                "file",
                "users-test.json",
                MediaType.APPLICATION_JSON_VALUE,
                jsonContent.getBytes()
            );
            
            // Mock service response: 3 total, 2 imported, 1 rejected
            BatchImportSummary mockSummary = new BatchImportSummary(3, 2, 1);
            when(userBatchService.importUsers(any())).thenReturn(mockSummary);
            
            // WHEN: Upload file to POST /api/users/batch
            mockMvc.perform(multipart("/api/users/batch")
                    .file(file))
                    
                    // THEN: Verify response
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.total").value(3))
                    .andExpect(jsonPath("$.imported").value(2))
                    .andExpect(jsonPath("$.rejected").value(1));
        }
        
        @Test
        void shouldRejectEmptyFile() throws Exception {
            // GIVEN: Empty file
            MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.json",
                MediaType.APPLICATION_JSON_VALUE,
                new byte[0]
            );
            
            // WHEN: Upload empty file
            mockMvc.perform(multipart("/api/users/batch")
                    .file(emptyFile))
                    
                    // THEN: Returns 400
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid request"))
                    .andExpect(jsonPath("$.message").value(containsString("cannot be empty")));
        }
        
        @Test
        void shouldRejectInvalidJson() throws Exception {
            // GIVEN: File with invalid JSON
            MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "invalid.json",
                MediaType.APPLICATION_JSON_VALUE,
                "{ this is not valid json }".getBytes()
            );
            
            // WHEN: Upload invalid JSON
            mockMvc.perform(multipart("/api/users/batch")
                    .file(invalidFile))
                    
                    // THEN: Returns 500
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.error").value("Import failed"))
                    .andExpect(jsonPath("$.message").exists());
        }
    }
    
    // ========================
    // HELPER METHODS
    // ========================
    
    /**
     * Creates mock GeneratedUser objects for testing.
     * 
     * @param count number of users to create
     * @return List of mock users
     */
    private List<GeneratedUser> createMockUsers(int count) {
        GeneratedUser user = new GeneratedUser();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setCity("New York");
        user.setCountry("US");
        user.setAvatar("https://example.com/avatar.jpg");
        user.setCompany("TechCorp");
        user.setJobPosition("Developer");
        user.setMobile("+1234567890");
        user.setUsername("john.doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("pass123");
        user.setRole("user");
        
        // Create list with 'count' copies
        GeneratedUser[] usersArray = new GeneratedUser[count];
        Arrays.fill(usersArray, user);
        return Arrays.asList(usersArray);
    }
}
