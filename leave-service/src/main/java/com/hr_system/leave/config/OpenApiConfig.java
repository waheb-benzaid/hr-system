package com.hr_system.leave.config;

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
    public OpenAPI leaveServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Leave Service API")
                        .description(
                                "REST API for managing employee leave requests. " +
                                "Communicates synchronously with the Employee Service to validate employees before creating leave requests. " +
                                "Leave status follows a strict flow: PENDING → APPROVED or PENDING → REJECTED (irreversible)."
                        )
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("HR System")
                                .email("hr@example.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local Development Server")
                ));
    }
}
