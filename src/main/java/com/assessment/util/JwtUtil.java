package com.assessment.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for JWT token operations.
 */
@Component
public class JwtUtil {

	@Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong12345}")
	private String secret;

	@Value("${jwt.expiration:86400000}")
	private long expirationMs;

	public String generateToken(String email, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);

		return Jwts.builder()
				.setClaims(claims)
				.setSubject(email)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationMs))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public String extractEmail(String token) {
		return extractAllClaims(token).getSubject();
	}

	public String extractRole(String token) {
		return extractAllClaims(token).get("role", String.class);
	}

	public Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public boolean validateToken(String token, String email) {
		try {
			String tokenEmail = extractEmail(token);
			return (tokenEmail.equals(email) && !isTokenExpired(token));
		} catch (Exception e) {
			return false;
		}
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}
}
