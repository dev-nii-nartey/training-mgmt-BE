package com.amponsem.curriculum_management.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Cohort Management API",
                version = "1.0",
                description = "API documentation for cohort Service"
        )
)
public class OpenApiConfig {
}