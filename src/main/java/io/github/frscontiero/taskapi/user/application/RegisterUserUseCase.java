package io.github.frscontiero.taskapi.user.application;

import io.github.frscontiero.taskapi.user.domain.EmailAlreadyRegisteredException;
import io.github.frscontiero.taskapi.user.domain.PasswordHasher;
import io.github.frscontiero.taskapi.user.domain.User;
import io.github.frscontiero.taskapi.user.domain.UserRepository;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCase {

  private final UserRepository users;
  private final PasswordHasher passwordHasher;
  private final Clock clock;

  public RegisterUserUseCase(UserRepository users, PasswordHasher passwordHasher, Clock clock) {
    this.users = users;
    this.passwordHasher = passwordHasher;
    this.clock = clock;
  }

  @Transactional
  public User execute(RegisterUserCommand command) {
    if (users.existsByEmail(command.email())) {
      throw new EmailAlreadyRegisteredException();
    }
    User user =
        User.register(
            command.name(),
            command.email(),
            passwordHasher.hash(command.password()),
            Instant.now(clock));
    return users.save(user);
  }
}
