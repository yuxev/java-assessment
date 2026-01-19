package com.assessment.service;

import com.assessment.dto.GeneratedUser;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class UserGenerator {

	private final Faker faker;
	private final Random random;

	public UserGenerator(Faker faker) {
		this.faker = faker;
		this.random = new Random();
	}

	public GeneratedUser generateOne() {
		LocalDate birthDate = faker.date()
				.birthday(18, 75)
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();

		int passwordLength = 6 + random.nextInt(5);
		String password = generatePassword(passwordLength);

		String role = random.nextDouble() < 0.2 ? "admin" : "user";
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
				role);
	}

	private String generatePassword(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder password = new StringBuilder();
		for (int i = 0; i < length; i++) {
			password.append(chars.charAt(random.nextInt(chars.length())));
		}
		return password.toString();
	}

	public List<GeneratedUser> generateMany(int count) {
		if (count < 1 || count > 500) {
			throw new IllegalArgumentException(
					"Count must be between 1 and 500. Requested: " + count);
		}

		List<GeneratedUser> users = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			users.add(generateOne());
		}

		return users;
	}
}
