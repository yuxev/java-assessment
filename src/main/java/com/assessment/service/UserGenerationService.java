package com.assessment.service;

import com.assessment.dto.GeneratedUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for user generation business logic.
 * 
 * WHY SEPARATE SERVICE FROM GENERATOR?
 * - UserGenerator: HOW to create users (technical implementation)
 * - UserGenerationService: WHEN/WHY to create users (business logic)
 * - Controller: HTTP handling and response formatting
 * 
 * This follows Single Responsibility and keeps code organized.
 * 
 * INTERVIEW TALKING POINT:
 * "The service layer is where business logic lives. It orchestrates
 * components like UserGenerator and applies business rules. Controllers
 * should be thin - they just handle HTTP concerns and delegate to services."
 */
@Service
public class UserGenerationService {
    
    private final UserGenerator userGenerator;
    
    /**
     * Constructor injection of UserGenerator component.
     * 
     * WHY INJECT INSTEAD OF 'new UserGenerator()'?
     * - Spring manages the lifecycle
     * - Easy to mock in tests
     * - Follows Dependency Inversion Principle
     */
    public UserGenerationService(UserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }
    
    /**
     * Generates a specified number of users.
     * 
     * BUSINESS LOGIC:
     * - Delegates to UserGenerator for actual creation
     * - Could add additional logic here like:
     *   - Logging generation requests
     *   - Rate limiting
     *   - Analytics tracking
     *   - Caching frequently requested counts
     * 
     * @param count number of users to generate
     * @return List of generated users
     * @throws IllegalArgumentException if count is invalid (handled by generator)
     */
    public List<GeneratedUser> generateUsers(int count) {
        return userGenerator.generateMany(count);
    }
}
