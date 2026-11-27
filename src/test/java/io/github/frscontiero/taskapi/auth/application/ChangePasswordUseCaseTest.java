package io.github.frscontiero.taskapi.auth.application;

import static io.github.frscontiero.taskapi.auth.application.AuthTestContext.NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.frscontiero.taskapi.auth.domain.IncorrectCurrentPasswordException;
import io.github.frscontiero.taskapi.user.UserFixtures;
import io.github.frscontiero.taskapi.user.domain.User;
import io.github.frscontiero.taskapi.user.domain.UserNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ChangePasswordUseCaseTest {

  private final AuthTestContext context = new AuthTestContext();
  private final ChangePasswordUseCase changePassword =
      new ChangePasswordUseCase(
          context.users, context.passwordHasher, context.refreshTokens, context.clock);
  private final User maria = context.users.save(UserFixtures.maria());

  @Test
  void shouldStoreHashOfNewPassword() {
    changePassword.execute(command(UserFixtures.MARIA_PASSWORD, "n0va-s3nha"));

    assertThat(context.passwordHasher.matches("n0va-s3nha", maria.getPasswordHash())).isTrue();
  }

  @Test
  void shouldEndAllSessionsOfUser() {
    context.tokenPairs.issueFor(maria.getId());
    context.tokenPairs.issueFor(maria.getId());

    changePassword.execute(command(UserFixtures.MARIA_PASSWORD, "n0va-s3nha"));

    assertThat(context.refreshTokens.activeOf(maria.getId(), NOW)).isEmpty();
  }

  @Test
  void shouldRejectIncorrectCurrentPassword() {
    assertThatThrownBy(() -> changePassword.execute(command("wrong-password", "n0va-s3nha")))
        .isInstanceOf(IncorrectCurrentPasswordException.class);
  }

  @Test
  void shouldKeepSessionsWhenCurrentPasswordIsIncorrect() {
    context.tokenPairs.issueFor(maria.getId());

    assertThatThrownBy(() -> changePassword.execute(command("wrong-password", "n0va-s3nha")))
        .isInstanceOf(IncorrectCurrentPasswordException.class);
    assertThat(context.refreshTokens.activeOf(maria.getId(), NOW)).hasSize(1);
  }

  @Test
  void shouldThrowWhenUserDoesNotExist() {
    var command = new ChangePasswordCommand(UUID.randomUUID(), "any", "n0va-s3nha");

    assertThatThrownBy(() -> changePassword.execute(command))
        .isInstanceOf(UserNotFoundException.class);
  }

  private ChangePasswordCommand command(String currentPassword, String newPassword) {
    return new ChangePasswordCommand(maria.getId(), currentPassword, newPassword);
  }
}
