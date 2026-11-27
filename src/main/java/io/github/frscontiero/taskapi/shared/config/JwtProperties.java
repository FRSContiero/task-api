package io.github.frscontiero.taskapi.shared.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** JWT settings. The secret comes from the JWT_SECRET environment variable outside dev/tests. */
@Validated
@ConfigurationProperties("app.jwt")
public record JwtProperties(
    // HS256 requires a key of at least 256 bits
    @NotBlank @Size(min = 32) String secret,
    @NotNull Duration accessTokenTtl,
    @NotNull Duration refreshTokenTtl) {}
