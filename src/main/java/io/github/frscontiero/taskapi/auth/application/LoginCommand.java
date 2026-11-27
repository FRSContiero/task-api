package io.github.frscontiero.taskapi.auth.application;

import io.github.frscontiero.taskapi.user.domain.Email;

public record LoginCommand(Email email, String password) {}
