package com.assessment.config;

import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for JavaFaker library.
 * 
 * WHAT IS @Configuration?
 * Tells Spring: "This class contains bean definitions"
 * Think of it like a factory that creates objects Spring will manage
 * 
 * WHAT IS @Bean?
 * Tells Spring: "Create this object and add it to the application context"
 * Any class can then @Autowire this Faker instance
 * 
 * WHY USE A BEAN?
 * - Single instance shared across the app (efficient)
 * - Easy to inject anywhere with @Autowired
 * - Easy to mock in tests
 * 
 * INTERVIEW TALKING POINT:
 * "Spring's dependency injection lets us centralize object creation.
 * By making Faker a bean, any service can inject it without creating
 * new instances everywhere. This follows the Singleton pattern and
 * makes testing easier because we can replace the bean with a mock."
 */
@Configuration
public class FakerConfig {
    
    /**
     * Creates a Faker bean for generating realistic fake data.
     * 
     * @return Faker instance that can be injected anywhere
     */
    @Bean
    public Faker faker() {
        return new Faker();
    }
}
