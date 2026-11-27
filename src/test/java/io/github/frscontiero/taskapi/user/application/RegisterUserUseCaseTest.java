package io.github.frscontiero.taskapi.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.frscontiero.taskapi.user.UserFixtures;
import io.github.frscontiero.taskapi.user.domain.Email;
import io.github.frscontiero.taskapi.user.domain.EmailAlreadyRegisteredException;
import io.github.frscontiero.taskapi.user.domain.User;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class RegisterUserUseCaseTest {

  private static final Instant NOW = Instant.parse("2026-11-26T10:00:00Z");

  private final InMemoryUserRepository users = new InMemoryUserRepository();
  private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
  private final RegisterUserUseCase registerUser =
      new RegisterUserUseCase(users, rawPassword -> "hashed:" + rawPassword, clock);

  @Test
  void shouldRegisterUserWithHashedPassword() {
    User user = registerUser.execute(command("ana@example.com"));

    assertThat(users.findById(user.getId())).contains(user);
    assertThat(user.getPasswordHash()).isEqualTo("hashed:s3nha-segura");
  }

  @Test
  void shouldUseClockForRegistrationTime() {
    User user = registerUser.execute(command("ana@example.com"));

    assertThat(user.getCreatedAt()).isEqualTo(NOW);
  }

  @Test
  void shouldRejectEmailAlreadyRegistered() {
    users.save(UserFixtures.maria());

    assertThatThrownBy(() -> registerUser.execute(command("MARIA.SOUZA@example.com")))
        .isInstanceOf(EmailAlreadyRegisteredException.class);
  }

  private static RegisterUserCommand command(String email) {
    return new RegisterUserCommand("Ana Lima", new Email(email), "s3nha-segura");
  }
}
