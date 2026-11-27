package io.github.frscontiero.taskapi.user.application;

import io.github.frscontiero.taskapi.user.domain.Email;

public record RegisterUserCommand(String name, Email email, String password) {}
