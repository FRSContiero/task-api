package io.github.frscontiero.taskapi.auth.application;

import java.util.UUID;

public record LogoutCommand(UUID userId, String refreshToken) {}
