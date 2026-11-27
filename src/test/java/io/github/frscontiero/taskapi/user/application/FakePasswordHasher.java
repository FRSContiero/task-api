package io.github.frscontiero.taskapi.user.application;

import io.github.frscontiero.taskapi.user.domain.PasswordHasher;

/** Deterministic and fast stand-in for BCrypt: the hash is the password with a prefix. */
public class FakePasswordHasher implements PasswordHasher {

  private static final String PREFIX = "hashed:";

  @Override
  public String hash(String rawPassword) {
    return PREFIX + rawPassword;
  }

  @Override
  public boolean matches(String rawPassword, String passwordHash) {
    return passwordHash.equals(hash(rawPassword));
  }
}
