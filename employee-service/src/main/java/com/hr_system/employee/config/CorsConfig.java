package com.hr_system.employee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow the swagger-hub page (served from either service) to fetch API docs cross-origin
        registry.addMapping("/v3/api-docs/**")
                .allowedOriginPatterns("http://localhost:*")
                .allowedMethods("GET");
    }
}
