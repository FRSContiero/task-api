package io.github.frscontiero.taskapi.auth.api;

import io.github.frscontiero.taskapi.auth.application.LoginCommand;
import io.github.frscontiero.taskapi.user.domain.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Schema(example = "maria.souza@example.com") @NotBlank @jakarta.validation.constraints.Email
        String email,
    @Schema(example = "s3nha-segura") @NotBlank String password) {

  LoginCommand toCommand() {
    return new LoginCommand(new Email(email), password);
  }
}
