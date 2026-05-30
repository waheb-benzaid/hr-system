package com.hr_system.employee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Service API")
                        .description(
                                "REST API for managing employees in the HR system. " +
                                "Supports creating, retrieving, updating, and soft-deleting employees. " +
                                "All IDs are UUIDs. Soft-delete preserves employee records for audit purposes."
                        )
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("HR System")
                                .email("hr@example.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local Development Server")
                ));
    }
}
