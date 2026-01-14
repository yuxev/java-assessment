package com.assessment.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for User endpoints.
 * 
 * @RestController = @Controller + @ResponseBody
 * Means: All methods return JSON automatically (not HTML views)
 * 
 * @RequestMapping - sets base path for all endpoints in this controller
 */

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    /**
     * TEST ENDPOINT - Just to verify everything works!
     * 
     * @GetMapping - handles HTTP GET requests
     * URL: GET http://localhost:9090/api/users/test
     * 
     * Returns: JSON object
     */
    @GetMapping("/test")
    public Map<String, String> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Spring Boot is running! 🚀");
        return response;
    }
    
    /**
     * ENDPOINT #1: Generate Users (placeholder for now)
     * 
     * @RequestParam - extracts query parameter from URL
     * URL: GET http://localhost:9090/api/users/generate?count=5
     * 
     * @param count - number of users to generate
     * @return - placeholder message (we'll implement file generation in Step 3)
     */
    @GetMapping("/generate")
    public Map<String, Object> generateUsers(@RequestParam(defaultValue = "10") int count) {
        // For now, just return a message
        // We'll implement actual file generation in Step 3
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Generate endpoint - will create " + count + " users");
        response.put("count", count);
        response.put("status", "placeholder - will implement in Step 3");
        return response;
    }
}
