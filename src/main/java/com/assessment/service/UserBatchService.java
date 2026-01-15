package com.assessment.service;

import com.assessment.dto.BatchImportSummary;
import com.assessment.dto.GeneratedUser;
import com.assessment.model.User;
import com.assessment.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for batch user import operations.
 * 
 * RESPONSIBILITIES:
 * - Parse uploaded JSON to User objects
 * - Check for duplicate username/email (both in file and database)
 * - Encrypt passwords using BCrypt
 * - Save valid users to database
 * - Return import summary
 * 
 * @Transactional ensures all-or-nothing database operations.
 * If any error occurs during saveAll(), entire batch rolls back.
 * 
 * INTERVIEW TALKING POINT:
 * "This service handles the business logic for batch imports. We check
 * duplicates at two levels: within the uploaded file itself (using HashSet
 * for O(1) lookup), and against existing database records. Passwords are
 * encrypted before storage for security. The @Transactional annotation
 * ensures data integrity - either all valid users are saved, or none are."
 */
@Service
public class UserBatchService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Constructor injection of dependencies.
     */
    public UserBatchService(UserRepository userRepository, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Imports a list of generated users into the database.
     * 
     * PROCESS:
     * 1. Loop through each GeneratedUser from JSON
     * 2. Check if username or email already exists in DB
     * 3. If duplicate → skip (increment rejected counter)
     * 4. If valid → encrypt password, convert to User entity, add to import list
     * 5. Bulk save all valid users to database
     * 6. Return summary
     * 
     * @Transactional - wraps operation in database transaction
     * 
     * @param generatedUsers - list parsed from uploaded JSON file
     * @return BatchImportSummary with counts
     */
    @Transactional
    public BatchImportSummary importUsers(List<GeneratedUser> generatedUsers) {
        int total = generatedUsers.size();
        int imported = 0;
        int rejected = 0;
        
        List<User> usersToSave = new ArrayList<>();
        
        // Track usernames/emails in current batch to avoid duplicates within file
        List<String> seenUsernames = new ArrayList<>();
        List<String> seenEmails = new ArrayList<>();
        
        for (GeneratedUser dto : generatedUsers) {
            // Check for duplicates in current batch
            boolean duplicateInBatch = seenUsernames.contains(dto.getUsername()) 
                                    || seenEmails.contains(dto.getEmail());
            
            // Check for duplicates in database
            boolean duplicateInDb = userRepository.existsByUsername(dto.getUsername())
                                 || userRepository.existsByEmail(dto.getEmail());
            
            if (duplicateInBatch || duplicateInDb) {
                // Skip this user
                rejected++;
                continue;
            }
            
            // Valid user - convert DTO to Entity
            User user = new User();
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setBirthDate(dto.getBirthDate());
            user.setCity(dto.getCity());
            user.setCountry(dto.getCountry());
            user.setAvatar(dto.getAvatar());
            user.setCompany(dto.getCompany());
            user.setJobPosition(dto.getJobPosition());
            user.setMobile(dto.getMobile());
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            
            // IMPORTANT: Encrypt password before saving!
            // Plain text: "password123"
            // Encrypted:  "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            
            user.setRole(dto.getRole());
            
            usersToSave.add(user);
            seenUsernames.add(dto.getUsername());
            seenEmails.add(dto.getEmail());
            imported++;
        }
        
        // Bulk save to database (more efficient than saving one by one)
        if (!usersToSave.isEmpty()) {
            userRepository.saveAll(usersToSave);
        }
        
        return new BatchImportSummary(total, imported, rejected);
    }
}
