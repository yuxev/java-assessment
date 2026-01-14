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
        System.out.println("\n Application started successfully!");
        System.out.println(" Server running on: http://localhost:9090");
        System.out.println(" H2 Console available at: http://localhost:9090/h2-console");
    }
}
