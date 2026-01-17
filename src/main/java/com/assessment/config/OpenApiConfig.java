package com.assessment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration.
 * Configures the server URL for Codespaces environment.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Use empty server URL - Swagger will use the same origin as the UI
        // This works in Codespaces because Swagger UI and API are on the same tunnel URL
        return new OpenAPI()
                .info(new Info()
                        .title("User Management API")
                        .version("1.0")
                        .description("REST API for user generation, authentication, and profile management"));
        // No servers configured = uses same origin as Swagger UI
    }
}
