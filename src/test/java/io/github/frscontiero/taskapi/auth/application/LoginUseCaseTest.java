package io.github.frscontiero.taskapi.auth.application;

import static io.github.frscontiero.taskapi.auth.application.AuthTestContext.NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.frscontiero.taskapi.auth.domain.InvalidCredentialsException;
import io.github.frscontiero.taskapi.auth.domain.OpaqueTokens;
import io.github.frscontiero.taskapi.user.UserFixtures;
import io.github.frscontiero.taskapi.user.domain.Email;
import io.github.frscontiero.taskapi.user.domain.User;
import org.junit.jupiter.api.Test;

class LoginUseCaseTest {

  private final AuthTestContext context = new AuthTestContext();
  private final LoginUseCase login =
      new LoginUseCase(context.users, context.passwordHasher, context.tokenPairs);
  private final User maria = context.users.save(UserFixtures.maria());

  @Test
  void shouldIssueAccessTokenForUser() {
    TokenPair tokens =
        login.execute(command("maria.souza@example.com", UserFixtures.MARIA_PASSWORD));

    assertThat(tokens.accessToken().value()).isEqualTo("access-" + maria.getId());
  }

  @Test
  void shouldStoreOnlyHashOfRefreshToken() {
    TokenPair tokens =
        login.execute(command("maria.souza@example.com", UserFixtures.MARIA_PASSWORD));

    assertThat(context.refreshTokens.findByTokenHash(OpaqueTokens.hash(tokens.refreshToken())))
        .get()
        .satisfies(token -> assertThat(token.isActive(NOW)).isTrue());
  }

  @Test
  void shouldAcceptEmailInAnyCase() {
    TokenPair tokens =
        login.execute(command("MARIA.SOUZA@example.com", UserFixtures.MARIA_PASSWORD));

    assertThat(tokens.accessToken().value()).isEqualTo("access-" + maria.getId());
  }

  @Test
  void shouldRejectWrongPassword() {
    assertThatThrownBy(() -> login.execute(command("maria.souza@example.com", "wrong-password")))
        .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  void shouldRejectUnknownEmailWithSameError() {
    assertThatThrownBy(() -> login.execute(command("nobody@example.com", "any-password")))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessage("Invalid email or password");
  }

  private static LoginCommand command(String email, String password) {
    return new LoginCommand(new Email(email), password);
  }
}
