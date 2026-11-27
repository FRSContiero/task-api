package io.github.frscontiero.taskapi.auth.api;

import io.github.frscontiero.taskapi.auth.application.TokenPair;
import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(
    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzZjZjMmE5ZSJ9.c2lnbmF0dXJl")
        String accessToken,
    @Schema(example = "Bearer") String tokenType,
    @Schema(description = "Validade do access token em segundos", example = "900") long expiresIn,
    @Schema(example = "q1w2e3r4t5y6u7i8o9p0a1s2d3f4g5h6j7k8l9z0x1c") String refreshToken) {

  static TokenResponse from(TokenPair tokens) {
    return new TokenResponse(
        tokens.accessToken().value(),
        "Bearer",
        tokens.accessToken().expiresIn().toSeconds(),
        tokens.refreshToken());
  }
}
