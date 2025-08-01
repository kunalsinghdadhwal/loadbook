package com.kunal.loadbook.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Value("${spring.application.name}")
        private String appName;

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("LoadBook API")
                                                .description("Backend system for managing Load & Booking operations efficiently")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("LoadBook Team")
                                                                .email("support@loadbook.com")
                                                                .url("https://github.com/kunal/loadbook"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:8080")
                                                                .description("Development Server"),
                                                new Server()
                                                                .url("https://api.loadbook.com")
                                                                .description("Production Server")));
        }
}
