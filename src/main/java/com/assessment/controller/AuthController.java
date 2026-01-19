package com.assessment.controller;

import com.assessment.dto.AuthResponse;
import com.assessment.dto.LoginRequest;
import com.assessment.model.User;
import com.assessment.repository.UserRepository;
import com.assessment.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthController(UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/auth")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		try {
			Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

			if (userOpt.isEmpty()) {
				userOpt = userRepository.findByEmail(loginRequest.getUsername());
			}

			if (userOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(createErrorResponse("Authentication failed", "Invalid credentials"));
			}

			User user = userOpt.get();

			if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(createErrorResponse("Authentication failed", "Invalid credentials"));
			}

			String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

			return ResponseEntity.ok(new AuthResponse(token));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("Authentication error", e.getMessage()));
		}
	}

	private Map<String, String> createErrorResponse(String error, String message) {
		Map<String, String> response = new HashMap<>();
		response.put("error", error);
		response.put("message", message);
		return response;
	}
}
