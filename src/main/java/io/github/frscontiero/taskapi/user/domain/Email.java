package io.github.frscontiero.taskapi.user.domain;

import java.util.Locale;
import java.util.Objects;

/** Email address normalized to lowercase, so lookups and uniqueness ignore case. */
public record Email(String value) {

  public Email {
    Objects.requireNonNull(value, "email must not be null");
    value = value.strip().toLowerCase(Locale.ROOT);
    if (value.isEmpty() || !value.contains("@")) {
      throw new IllegalArgumentException("Invalid email address");
    }
  }
}
