package com.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		String codespaceName = System.getenv("CODESPACE_NAME");
		String baseUrl;

		if (codespaceName != null && !codespaceName.isEmpty()) {
			// GitHub Codespaces URL format
			baseUrl = "https://" + codespaceName + "-9090.app.github.dev";
		} else {
			// Local development
			baseUrl = "http://localhost:9090";
		}

		System.out.println("\n✅ Application started successfully!");
		System.out.println("📡 Server running on: " + baseUrl);
		System.out.println("🗄️  H2 Console: " + baseUrl + "/h2-console");
		System.out.println("🔧 Test endpoint: " + baseUrl + "/api/users/test");
		System.out.println("👥 Generate users: " + baseUrl + "/api/users/generate?count=5");
	}
}
