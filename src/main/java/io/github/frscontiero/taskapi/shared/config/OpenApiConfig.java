package io.github.frscontiero.taskapi.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  /** Referenced by {@code @SecurityRequirement} on authenticated endpoints. */
  public static final String BEARER_AUTH = "bearerAuth";

  @Bean
  OpenAPI openApi(@Value("${spring.application.name}") String applicationName) {
    return new OpenAPI()
        .info(
            new Info()
                .title(applicationName)
                .version("v1")
                .description("API de gerenciamento de tarefas com autenticação JWT"))
        .components(
            new Components()
                .addSecuritySchemes(
                    BEARER_AUTH,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }
}
