package com.assessment.dto;

import java.time.LocalDate;

/**
 * DTO for generated users (API #1: GET /api/users/generate)
 * 
 * WHY THIS DTO?
 * - Matches the exact JSON structure required by the assessment
 * - NO 'id' field because users aren't saved to database yet
 * - Password is PLAIN TEXT (will be encrypted later during batch import)
 * - Clean separation: this is the API contract, not the database entity
 * 
 * INTERVIEW TALKING POINT:
 * "DTOs decouple API structure from database schema. This DTO represents
 * the generation response format, while the User entity represents the
 * database table structure. They serve different purposes."
 */
public class GeneratedUser {
    
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String city;
    private String country;      // ISO2 code like "US", "FR", "MA"
    private String avatar;        // Image URL from Faker
    private String company;
    private String jobPosition;
    private String mobile;
    private String username;
    private String email;
    private String password;      // Plain text, 6-10 characters
    private String role;          // "admin" or "user"

    /**
     * Default constructor required by Jackson for JSON serialization.
     * Jackson uses this + setters to convert Java object → JSON
     */
    public GeneratedUser() {
    }

    /**
     * All-args constructor for easy object creation.
     * Used by UserGenerator to build instances.
     */
    public GeneratedUser(String firstName, String lastName, LocalDate birthDate,
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

    // Getters and Setters
    // Why needed? Jackson serializer uses getters to convert object to JSON
    
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
}
