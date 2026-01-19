package com.assessment.repository;

import com.assessment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);
}
