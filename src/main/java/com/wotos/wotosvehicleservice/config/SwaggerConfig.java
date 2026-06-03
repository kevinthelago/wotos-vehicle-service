package com.wotos.wotosvehicleservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (springdoc) configuration. Replaces the legacy SpringFox {@code Docket}
 * removed during the Spring Boot 3.2 migration — springdoc auto-configures the
 * {@code /v3/api-docs} and {@code /swagger-ui} endpoints; this bean only supplies
 * the API metadata shown in the Swagger UI.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI vehicleServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WoTOS Vehicle Service")
                        .description("Tankopedia mirror, armor profiles, and 3D model asset metadata.")
                        .version("v1"));
    }

}
