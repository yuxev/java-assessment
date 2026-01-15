package com.assessment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

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
 */
@Entity
@Table(name = "users")
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
    public User() {
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(String jobPosition) {
        this.jobPosition = jobPosition;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(firstName, user.firstName) &&
               Objects.equals(lastName, user.lastName) &&
               Objects.equals(birthDate, user.birthDate) &&
               Objects.equals(city, user.city) &&
               Objects.equals(country, user.country) &&
               Objects.equals(avatar, user.avatar) &&
               Objects.equals(company, user.company) &&
               Objects.equals(jobPosition, user.jobPosition) &&
               Objects.equals(mobile, user.mobile) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email) &&
               Objects.equals(password, user.password) &&
               Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, birthDate, city, country,
                avatar, company, jobPosition, mobile, username, email, password, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", avatar='" + avatar + '\'' +
                ", company='" + company + '\'' +
                ", jobPosition='" + jobPosition + '\'' +
                ", mobile='" + mobile + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
