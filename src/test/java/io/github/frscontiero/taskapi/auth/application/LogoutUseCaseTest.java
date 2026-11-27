package io.github.frscontiero.taskapi.auth.application;

import static io.github.frscontiero.taskapi.auth.application.AuthTestContext.NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class LogoutUseCaseTest {

  private final AuthTestContext context = new AuthTestContext();
  private final LogoutUseCase logout = new LogoutUseCase(context.refreshTokens, context.clock);
  private final UUID userId = UUID.randomUUID();

  @Test
  void shouldRevokeOwnRefreshToken() {
    String refreshToken = context.tokenPairs.issueFor(userId).refreshToken();

    logout.execute(new LogoutCommand(userId, refreshToken));

    assertThat(context.refreshTokens.activeOf(userId, NOW)).isEmpty();
  }

  @Test
  void shouldIgnoreTokenOfAnotherUser() {
    UUID otherUser = UUID.randomUUID();
    String othersToken = context.tokenPairs.issueFor(otherUser).refreshToken();

    logout.execute(new LogoutCommand(userId, othersToken));

    assertThat(context.refreshTokens.activeOf(otherUser, NOW)).hasSize(1);
  }

  @Test
  void shouldIgnoreUnknownToken() {
    assertThatCode(() -> logout.execute(new LogoutCommand(userId, "unknown-token")))
        .doesNotThrowAnyException();
  }
}
