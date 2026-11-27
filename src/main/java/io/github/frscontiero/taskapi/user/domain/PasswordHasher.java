package io.github.frscontiero.taskapi.user.domain;

public interface PasswordHasher {

  String hash(String rawPassword);
}
