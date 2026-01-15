package com.assessment.repository;

import com.assessment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity database operations.
 * 
 * WHAT IS JpaRepository?
 * - Spring Data JPA interface providing CRUD operations
 * - <User, Long> means: Entity type = User, Primary Key type = Long
 * - Spring auto-generates implementation at runtime (no code needed!)
 * 
 * AUTO-PROVIDED METHODS (inherited from JpaRepository):
 * - save(User user) - insert or update
 * - saveAll(List<User> users) - bulk insert/update
 * - findById(Long id) - find by primary key
 * - findAll() - get all users
 * - deleteById(Long id) - delete by primary key
 * - count() - count total users
 * - existsById(Long id) - check if exists
 * 
 * CUSTOM QUERY METHODS:
 * Spring auto-generates SQL based on method names!
 * - findByUsername → SELECT * FROM users WHERE username = ?
 * - existsByEmail → SELECT COUNT(*) > 0 FROM users WHERE email = ?
 * 
 * INTERVIEW TALKING POINTS:
 * "JpaRepository eliminates boilerplate JDBC code. Spring Data JPA
 * uses method name conventions to auto-generate queries. For example,
 * findByUsername automatically translates to SELECT * FROM users
 * WHERE username = ?. This follows the Repository pattern and keeps
 * database logic separated from business logic."
 * 
 * @Repository - marks this as a Spring-managed repository bean
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Check if a user with this email already exists in database.
     * 
     * USED FOR: Duplicate detection during batch import
     * 
     * Spring auto-generates:
     * SELECT COUNT(*) > 0 FROM users WHERE email = ?
     * 
     * @param email - email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if a user with this username already exists in database.
     * 
     * USED FOR: Duplicate detection during batch import
     * 
     * Spring auto-generates:
     * SELECT COUNT(*) > 0 FROM users WHERE username = ?
     * 
     * @param username - username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Find a user by username.
     * 
     * USED FOR: Authentication (login with username)
     * 
     * Spring auto-generates:
     * SELECT * FROM users WHERE username = ?
     * 
     * Optional<User> means: might return a user or might be empty
     * - user.isPresent() → found
     * - user.isEmpty() → not found
     * Safer than returning null!
     * 
     * @param username - username to search for
     * @return Optional containing User if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email.
     * 
     * USED FOR: Authentication (login with email instead of username)
     * 
     * Spring auto-generates:
     * SELECT * FROM users WHERE email = ?
     * 
     * @param email - email to search for
     * @return Optional containing User if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
}
