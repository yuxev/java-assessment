package com.assessment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * User Entity - represents the 'users' table in the database.
 * 
 * NESTJS EQUIVALENT:
 * This is like a TypeORM @Entity() class in NestJS - it defines both:
 * 1. The database table structure
 * 2. The object structure for your application
 * 
 * @Entity - marks this class as a JPA entity (database table)
 * @Table - specifies the table name in the database
 * @Data - Lombok annotation that auto-generates:
 *         - getters for all fields
 *         - setters for all fields
 *         - toString() method
 *         - equals() and hashCode() methods
 * @NoArgsConstructor - generates empty constructor: new User()
 * @AllArgsConstructor - generates constructor with all fields
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * Primary Key - Auto-incrementing ID
     * 
     * @Id - marks this as the primary key
     * @GeneratedValue - database auto-generates this value
     * IDENTITY strategy = auto_increment in MySQL/H2
     * 
     * C++ equivalent: unsigned long long id;
     * NestJS equivalent: @PrimaryGeneratedColumn()
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * First Name
     * @NotBlank - validation: cannot be null, empty, or just whitespace
     */
    @NotBlank(message = "First name is required")
    @Column(name = "first_name")
    private String firstName;
    
    /**
     * Last Name
     */
    @NotBlank(message = "Last name is required")
    @Column(name = "last_name")
    private String lastName;
    
    /**
     * Birth Date
     * LocalDate = Java's date type (year-month-day, no time)
     * 
     * NestJS equivalent: birthDate: Date;
     */
    @NotNull(message = "Birth date is required")
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    /**
     * City
     */
    @Column(name = "city")
    private String city;
    
    /**
     * Country (ISO2 code like "US", "FR", "MA")
     */
    @Column(name = "country", length = 2)
    private String country;
    
    /**
     * Avatar URL (image URL from JavaFaker)
     */
    @Column(name = "avatar")
    private String avatar;
    
    /**
     * Company name
     */
    @Column(name = "company")
    private String company;
    
    /**
     * Job Position/Title
     */
    @Column(name = "job_position")
    private String jobPosition;
    
    /**
     * Mobile phone number
     */
    @Column(name = "mobile")
    private String mobile;
    
    /**
     * Username - UNIQUE constraint (no duplicates allowed!)
     * Used for login authentication
     * 
     * @Column(unique = true) - creates UNIQUE constraint in database
     * Like: CREATE UNIQUE INDEX ON users(username)
     */
    @NotBlank(message = "Username is required")
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    
    /**
     * Email - UNIQUE constraint (no duplicates allowed!)
     * Used for login authentication
     * 
     * @Email - validates email format (must contain @, valid domain, etc.)
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    /**
     * Password - will be encrypted before saving to database
     * NEVER store passwords in plain text!
     * 
     * We'll use BCryptPasswordEncoder to hash this before saving
     * 
     * Note: @Size is for the ENCRYPTED password length
     * (BCrypt hashes are ~60 characters long)
     */
    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;
    
    /**
     * Role - either "admin" or "user"
     * Determines access permissions
     * 
     * For now it's a simple String
     * (In advanced apps, you'd use an Enum or separate Role entity)
     */
    @NotBlank(message = "Role is required")
    @Column(name = "role", nullable = false)
    private String role;
    
    /**
     * Custom constructor without ID (for creating new users)
     * ID is auto-generated, so we don't need it when creating new users
     * 
     * Useful when doing: new User(firstName, lastName, ...)
     */
    public User(String firstName, String lastName, LocalDate birthDate, 
                String city, String country, String avatar, String company,
                String jobPosition, String mobile, String username, 
                String email, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.city = city;
        this.country = country;
        this.avatar = avatar;
        this.company = company;
        this.jobPosition = jobPosition;
        this.mobile = mobile;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
