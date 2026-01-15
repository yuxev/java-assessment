package com.assessment.service;

import com.assessment.dto.GeneratedUser;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Component responsible for generating realistic fake user data.
 * 
 * WHY @Component?
 * Marks this as a Spring-managed bean so it can be injected into services
 * Similar to @Service but more generic (this is a utility, not business logic)
 * 
 * INTERVIEW TALKING POINT:
 * "This component encapsulates the data generation logic. By separating it
 * from the service layer, we follow Single Responsibility Principle - this
 * class only cares about HOW to generate users, while the service handles
 * WHEN and WHY to generate them."
 */
@Component
public class UserGenerator {
    
    private final Faker faker;
    private final Random random;
    
    /**
     * Constructor injection of Faker bean.
     * Spring automatically injects the Faker instance we defined in FakerConfig.
     * 
     * WHY CONSTRUCTOR INJECTION?
     * - Makes dependencies explicit and required
     * - Easier to test (can pass mock Faker in unit tests)
     * - Recommended over field injection (@Autowired on fields)
     */
    public UserGenerator(Faker faker) {
        this.faker = faker;
        this.random = new Random();
    }
    
    /**
     * Generates a single user with realistic fake data.
     * 
     * IMPLEMENTATION DETAILS:
     * - firstName/lastName: Real names from Faker
     * - birthDate: Random date between 1950 and 2005 (ages 18-75)
     * - city/country: Faker provides realistic locations
     * - avatar: Robohash generates unique avatar URLs
     * - password: Random 6-10 character alphanumeric string
     * - role: 20% chance of "admin", 80% "user" (realistic distribution)
     * 
     * @return GeneratedUser DTO with all fields populated
     */
    public GeneratedUser generateOne() {
        // Generate realistic birth date (18-75 years old)
        LocalDate birthDate = faker.date()
            .birthday(18, 75)
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
        
        // Generate password between 6-10 characters (alphanumeric)
        int passwordLength = 6 + random.nextInt(5); // 6-10
        String password = generatePassword(passwordLength);
        
        // Generate role: 20% admin, 80% user
        String role = random.nextDouble() < 0.2 ? "admin" : "user";
        
        // Get country code (ISO2 format like "US", "FR")
        String countryCode = faker.address().countryCode();
        
        return new GeneratedUser(
            faker.name().firstName(),
            faker.name().lastName(),
            birthDate,
            faker.address().city(),
            countryCode,
            faker.avatar().image(),
            faker.company().name(),
            faker.job().position(),
            faker.phoneNumber().phoneNumber(),
            faker.name().username(),
            faker.internet().emailAddress(),
            password,
            role
        );
    }
    
    /**
     * Generates a random alphanumeric password.
     * 
     * @param length desired password length
     * @return random password string
     */
    private String generatePassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
    
    /**
     * Generates a list of users.
     * 
     * VALIDATION:
     * - count must be between 1 and 500 (prevent abuse)
     * - Throws IllegalArgumentException with clear message if invalid
     * 
     * WHY VALIDATE HERE?
     * - Service layer enforces business rules
     * - Controller just passes the parameter
     * - Clear separation of concerns
     * 
     * @param count number of users to generate (1-500)
     * @return List of GeneratedUser DTOs
     * @throws IllegalArgumentException if count is out of valid range
     */
    public List<GeneratedUser> generateMany(int count) {
        // Validation
        if (count < 1 || count > 500) {
            throw new IllegalArgumentException(
                "Count must be between 1 and 500. Requested: " + count
            );
        }
        
        List<GeneratedUser> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            users.add(generateOne());
        }
        
        return users;
    }
}
