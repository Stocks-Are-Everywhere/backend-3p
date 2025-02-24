package org.scoula.three_people.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .servers(serverList());
    }

    private Info apiInfo() {
        return new Info()
                .title("Onseju API Test")
                .version("1.0.0");
    }

    private List<Server> serverList() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Local Server"),
                new Server()
                        .url("https://onseju.com")
                        .description("Production Server")
        );
    }
}
