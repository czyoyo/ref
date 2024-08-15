package com.example.ref.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    String jwt = "JWT";
    Components components = new Components().addSecuritySchemes(jwt,
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT"));

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .components(components)
            .info(apiInfo())
            .addSecurityItem(new SecurityRequirement().addList(jwt));
    }

    private Info apiInfo() {
        return new Info()
            .title("Springdoc")
            .description("Swagger UI")
            .version("1.0.0");
    }
}
