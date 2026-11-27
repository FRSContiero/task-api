package io.github.frscontiero.taskapi.auth.application;

import io.github.frscontiero.taskapi.auth.domain.IncorrectCurrentPasswordException;
import io.github.frscontiero.taskapi.auth.domain.RefreshTokenRepository;
import io.github.frscontiero.taskapi.user.domain.PasswordHasher;
import io.github.frscontiero.taskapi.user.domain.User;
import io.github.frscontiero.taskapi.user.domain.UserNotFoundException;
import io.github.frscontiero.taskapi.user.domain.UserRepository;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lives in auth rather than user because it must end the user's sessions; keeping it in user would
 * make the two features depend on each other.
 */
@Service
public class ChangePasswordUseCase {

  private final UserRepository users;
  private final PasswordHasher passwordHasher;
  private final RefreshTokenRepository refreshTokens;
  private final Clock clock;

  public ChangePasswordUseCase(
      UserRepository users,
      PasswordHasher passwordHasher,
      RefreshTokenRepository refreshTokens,
      Clock clock) {
    this.users = users;
    this.passwordHasher = passwordHasher;
    this.refreshTokens = refreshTokens;
    this.clock = clock;
  }

  @Transactional
  public void execute(ChangePasswordCommand command) {
    User user =
        users
            .findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException(command.userId()));
    if (!passwordHasher.matches(command.currentPassword(), user.getPasswordHash())) {
      throw new IncorrectCurrentPasswordException();
    }
    user.changePassword(passwordHasher.hash(command.newPassword()));
    refreshTokens.revokeAllActiveOf(user.getId(), Instant.now(clock));
  }
}
