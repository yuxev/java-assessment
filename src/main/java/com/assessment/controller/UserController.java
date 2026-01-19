package com.assessment.controller;

import com.assessment.dto.BatchImportSummary;
import com.assessment.dto.GeneratedUser;
import com.assessment.model.User;
import com.assessment.repository.UserRepository;
import com.assessment.service.UserBatchService;
import com.assessment.service.UserGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserGenerationService userGenerationService;
	private final UserBatchService userBatchService;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;

	public UserController(UserGenerationService userGenerationService,
			UserBatchService userBatchService,
			UserRepository userRepository,
			ObjectMapper objectMapper) {
		this.userGenerationService = userGenerationService;
		this.userBatchService = userBatchService;
		this.userRepository = userRepository;
		this.objectMapper = objectMapper;
	}

	@GetMapping("/generate")
	public ResponseEntity<?> generateUsers(@RequestParam(defaultValue = "10") int count) {
		try {
			List<GeneratedUser> users = userGenerationService.generateUsers(count);
			byte[] jsonBytes = objectMapper.writeValueAsBytes(users);
			String timestamp = LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
			String filename = "users-" + timestamp + ".json";
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.contentType(MediaType.APPLICATION_JSON)
					.body(jsonBytes);

		} catch (IllegalArgumentException e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Invalid request");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Internal server error");
			error.put("message", "Failed to generate users: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@Operation(summary = "Batch upload users from JSON file", description = "Upload a JSON file containing users (from /generate endpoint) to import into database")
	@PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> batchUpload(
			@Parameter(description = "JSON file containing user data", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestParam("file") MultipartFile file) {
		try {
			if (file.isEmpty()) {
				Map<String, String> error = new HashMap<>();
				error.put("error", "Invalid request");
				error.put("message", "File is required and cannot be empty");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
			}

			List<GeneratedUser> users = objectMapper.readValue(
					file.getBytes(),
					objectMapper.getTypeFactory().constructCollectionType(List.class, GeneratedUser.class));

			BatchImportSummary summary = userBatchService.importUsers(users);
			return ResponseEntity.ok(summary);

		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Import failed");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@GetMapping("/me")
	public ResponseEntity<?> getMyProfile(Authentication authentication) {
		try {
			if (authentication == null || !authentication.isAuthenticated()) {
				Map<String, String> error = new HashMap<>();
				error.put("error", "Unauthorized");
				error.put("message", "Valid JWT token required");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
			}

			String email = authentication.getName();

			User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new RuntimeException("User not found"));

			return ResponseEntity.ok(user);

		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Error retrieving profile");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@GetMapping("/{username}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
		try {
			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("User not found: " + username));

			return ResponseEntity.ok(user);

		} catch (RuntimeException e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "User not found");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

		} catch (Exception e) {
			Map<String, String> error = new HashMap<>();
			error.put("error", "Error retrieving user");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}
}
