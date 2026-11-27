package io.github.frscontiero.taskapi.auth.api;

import io.github.frscontiero.taskapi.auth.application.ChangePasswordCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ChangePasswordRequest(
    @Schema(example = "s3nha-segura") @NotBlank String currentPassword,
    // Same limits as registration: 72 is the maximum input length BCrypt takes into account
    @Schema(example = "n0va-s3nha-segura", minLength = 8, maxLength = 72)
        @NotBlank
        @Size(min = 8, max = 72)
        String newPassword) {

  ChangePasswordCommand toCommand(UUID userId) {
    return new ChangePasswordCommand(userId, currentPassword, newPassword);
  }
}
