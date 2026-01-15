package com.assessment.service;

import com.assessment.dto.GeneratedUser;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserGenerator component.
 * 
 * WHAT WE'RE TESTING:
 * - Correct number of users generated
 * - All fields are non-null
 * - Password length validation (6-10 chars)
 * - Role values are valid ("admin" or "user")
 * - Count validation (rejects invalid ranges)
 */
class UserGeneratorTest {
    
    private UserGenerator userGenerator;
    
    @BeforeEach
    void setUp() {
        // Create real UserGenerator with real Faker
        userGenerator = new UserGenerator(new Faker());
    }
    
    @Test
    void generateOne_shouldCreateUserWithAllFieldsPopulated() {
        // When: generate one user
        GeneratedUser user = userGenerator.generateOne();
        
        // Then: all fields should be non-null
        assertNotNull(user.getFirstName(), "First name should not be null");
        assertNotNull(user.getLastName(), "Last name should not be null");
        assertNotNull(user.getBirthDate(), "Birth date should not be null");
        assertNotNull(user.getCity(), "City should not be null");
        assertNotNull(user.getCountry(), "Country should not be null");
        assertNotNull(user.getAvatar(), "Avatar should not be null");
        assertNotNull(user.getCompany(), "Company should not be null");
        assertNotNull(user.getJobPosition(), "Job position should not be null");
        assertNotNull(user.getMobile(), "Mobile should not be null");
        assertNotNull(user.getUsername(), "Username should not be null");
        assertNotNull(user.getEmail(), "Email should not be null");
        assertNotNull(user.getPassword(), "Password should not be null");
        assertNotNull(user.getRole(), "Role should not be null");
    }
    
    @Test
    void generateOne_shouldCreatePasswordBetween6And10Characters() {
        // When: generate multiple users to test password randomness
        for (int i = 0; i < 10; i++) {
            GeneratedUser user = userGenerator.generateOne();
            
            // Then: password length should be 6-10 characters
            int passwordLength = user.getPassword().length();
            assertTrue(passwordLength >= 6 && passwordLength <= 10,
                "Password length should be between 6 and 10, but was: " + passwordLength);
        }
    }
    
    @Test
    void generateOne_shouldCreateValidRole() {
        // When: generate multiple users
        for (int i = 0; i < 20; i++) {
            GeneratedUser user = userGenerator.generateOne();
            
            // Then: role should be either "admin" or "user"
            assertTrue(
                user.getRole().equals("admin") || user.getRole().equals("user"),
                "Role should be 'admin' or 'user', but was: " + user.getRole()
            );
        }
    }
    
    @Test
    void generateMany_shouldCreateCorrectNumberOfUsers() {
        // Given: request 5 users
        int count = 5;
        
        // When: generate users
        List<GeneratedUser> users = userGenerator.generateMany(count);
        
        // Then: should return exactly 5 users
        assertEquals(count, users.size(), "Should generate exactly " + count + " users");
    }
    
    @Test
    void generateMany_shouldThrowExceptionWhenCountTooLow() {
        // When/Then: count < 1 should throw exception
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userGenerator.generateMany(0),
            "Should throw exception for count = 0"
        );
        
        assertTrue(exception.getMessage().contains("must be between 1 and 500"),
            "Error message should mention valid range");
    }
    
    @Test
    void generateMany_shouldThrowExceptionWhenCountTooHigh() {
        // When/Then: count > 500 should throw exception
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userGenerator.generateMany(501),
            "Should throw exception for count = 501"
        );
        
        assertTrue(exception.getMessage().contains("must be between 1 and 500"),
            "Error message should mention valid range");
    }
    
    @Test
    void generateMany_shouldAcceptValidCounts() {
        // When/Then: valid boundary values should work
        assertDoesNotThrow(() -> userGenerator.generateMany(1), "Should accept count = 1");
        assertDoesNotThrow(() -> userGenerator.generateMany(500), "Should accept count = 500");
        assertDoesNotThrow(() -> userGenerator.generateMany(100), "Should accept count = 100");
    }
}
