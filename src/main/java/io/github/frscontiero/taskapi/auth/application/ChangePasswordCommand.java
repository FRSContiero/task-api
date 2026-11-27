package io.github.frscontiero.taskapi.auth.application;

import java.util.UUID;

public record ChangePasswordCommand(UUID userId, String currentPassword, String newPassword) {}
