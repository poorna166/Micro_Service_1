package com.techcommerce.admin_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI adminAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Admin Service API")
                        .description("Admin panel for managing products, categories, and catalogs.")
                        .version("1.0"));
    }
}
