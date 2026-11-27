package io.github.frscontiero.taskapi.user.api;

import io.github.frscontiero.taskapi.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    @Schema(example = "3f6c2a9e-8d1b-4c7a-9f0e-2b5d6a7c8e91") UUID id,
    @Schema(example = "Maria Souza") String name,
    @Schema(example = "maria.souza@example.com") String email,
    @Schema(example = "2026-11-26T14:30:00Z") Instant createdAt) {

  static UserResponse from(User user) {
    return new UserResponse(
        user.getId(), user.getName(), user.getEmail().value(), user.getCreatedAt());
  }
}
