package io.github.frscontiero.taskapi.user;

import io.github.frscontiero.taskapi.user.domain.Email;
import io.github.frscontiero.taskapi.user.domain.User;
import java.time.Instant;

public final class UserFixtures {

  public static final Instant REGISTERED_AT = Instant.parse("2026-11-26T14:30:00Z");

  /** Plain password of {@link #maria()}; her hash follows {@code FakePasswordHasher}. */
  public static final String MARIA_PASSWORD = "s3nha-da-maria";

  private UserFixtures() {}

  public static User maria() {
    return User.register(
        "Maria Souza",
        new Email("maria.souza@example.com"),
        "hashed:" + MARIA_PASSWORD,
        REGISTERED_AT);
  }
}
