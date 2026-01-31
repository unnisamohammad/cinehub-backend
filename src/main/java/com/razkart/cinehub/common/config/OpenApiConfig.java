package com.razkart.cinehub.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token")
                        )
                )
                .info(new Info()
                        .title("CineHub API")
                        .version("1.0.0")
                        .description("Movie Ticketing Platform API - Book tickets for movies, concerts, sports and more")
                        .contact(new Contact()
                                .name("CineHub Support")
                                .email("support@cinehub.com")
                        )
                        .license(new License()
                                .name("Proprietary")
                        )
                );
    }
}
