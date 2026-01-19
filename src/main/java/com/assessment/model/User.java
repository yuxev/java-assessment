package com.assessment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * User entity representing the users table.
 */
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "First name is required")
	@Column(name = "first_name")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Column(name = "last_name")
	private String lastName;

	@NotNull(message = "Birth date is required")
	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(name = "city")
	private String city;

	@Column(name = "country", length = 2)
	private String country;

	@Column(name = "avatar")
	private String avatar;

	@Column(name = "company")
	private String company;

	@Column(name = "job_position")
	private String jobPosition;

	@Column(name = "mobile")
	private String mobile;

	@NotBlank(message = "Username is required")
	@Column(name = "username", unique = true, nullable = false)
	private String username;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@NotBlank(message = "Password is required")
	@Column(name = "password", nullable = false)
	private String password;

	@NotBlank(message = "Role is required")
	@Column(name = "role", nullable = false)
	private String role;

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
