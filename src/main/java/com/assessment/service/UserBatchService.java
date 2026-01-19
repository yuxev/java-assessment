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
 */
@Service
public class UserBatchService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserBatchService(UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public BatchImportSummary importUsers(List<GeneratedUser> generatedUsers) {
		int total = generatedUsers.size();
		int imported = 0;
		int rejected = 0;

		List<User> usersToSave = new ArrayList<>();
		List<String> seenUsernames = new ArrayList<>();
		List<String> seenEmails = new ArrayList<>();

		for (GeneratedUser dto : generatedUsers) {
			boolean duplicateInBatch = seenUsernames.contains(dto.getUsername())
					|| seenEmails.contains(dto.getEmail());

			boolean duplicateInDb = userRepository.existsByUsername(dto.getUsername())
					|| userRepository.existsByEmail(dto.getEmail());

			if (duplicateInBatch || duplicateInDb) {
				rejected++;
				continue;
			}

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

			user.setPassword(passwordEncoder.encode(dto.getPassword()));

			user.setRole(dto.getRole());

			usersToSave.add(user);
			seenUsernames.add(dto.getUsername());
			seenEmails.add(dto.getEmail());
			imported++;
		}

		if (!usersToSave.isEmpty()) {
			userRepository.saveAll(usersToSave);
		}

		return new BatchImportSummary(total, imported, rejected);
	}
}
