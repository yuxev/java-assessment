package com.assessment.service;

import com.assessment.dto.GeneratedUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGenerationService {

	private final UserGenerator userGenerator;

	public UserGenerationService(UserGenerator userGenerator) {
		this.userGenerator = userGenerator;
	}

	public List<GeneratedUser> generateUsers(int count) {
		return userGenerator.generateMany(count);
	}
}
