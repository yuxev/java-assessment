package com.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of the Spring Boot application.
 * 
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 * This single annotation does all the Spring Boot magic:
 * - Scans for components (controllers, services, etc.)
 * - Auto-configures based on dependencies (web, database, etc.)
 * - Enables Spring Boot features
 */
@SpringBootApplication
public class Application {
    
    /**
     * Main method - starts the Spring Boot application.
     * Think of this like your main() in C++ or main.ts in NestJS.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        
        // Detect if running in GitHub Codespaces
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
