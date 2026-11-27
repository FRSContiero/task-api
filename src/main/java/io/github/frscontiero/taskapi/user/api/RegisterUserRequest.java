package io.github.frscontiero.taskapi.user.api;

import io.github.frscontiero.taskapi.user.application.RegisterUserCommand;
import io.github.frscontiero.taskapi.user.domain.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
    @Schema(example = "Maria Souza") @NotBlank @Size(max = 100) String name,
    @Schema(example = "maria.souza@example.com")
        @NotBlank
        @jakarta.validation.constraints.Email
        @Size(max = 254)
        String email,
    // 72 is the maximum input length BCrypt takes into account
    @Schema(example = "s3nha-segura", minLength = 8, maxLength = 72)
        @NotBlank
        @Size(min = 8, max = 72)
        String password) {

  RegisterUserCommand toCommand() {
    return new RegisterUserCommand(name, new Email(email), password);
  }
}
