package com.fridgemate.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fridgeMateOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FridgeMate API")
                        .description("냉장고 재고관리 + AI 레시피 추천 시스템")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("FridgeMate Team")
                                .email("contact@fridgemate.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버"),
                        new Server()
                                .url("https://api.fridgemate.com")
                                .description("운영 서버")
                ));
    }
}
