package io.github.frscontiero.taskapi.auth.application;

import static io.github.frscontiero.taskapi.auth.application.AuthTestContext.NOW;
import static io.github.frscontiero.taskapi.auth.application.AuthTestContext.REFRESH_TTL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.frscontiero.taskapi.auth.domain.InvalidRefreshTokenException;
import io.github.frscontiero.taskapi.auth.domain.OpaqueTokens;
import io.github.frscontiero.taskapi.auth.domain.RefreshToken;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RefreshTokensUseCaseTest {

  private final AuthTestContext context = new AuthTestContext();
  private final RefreshTokensUseCase refresh =
      new RefreshTokensUseCase(context.refreshTokens, context.tokenPairs, context.clock);
  private final UUID userId = UUID.randomUUID();

  @Test
  void shouldIssueNewPairForSameUser() {
    String refreshToken = context.tokenPairs.issueFor(userId).refreshToken();

    TokenPair tokens = refresh.execute(refreshToken);

    assertThat(tokens.accessToken().value()).isEqualTo("access-" + userId);
    assertThat(tokens.refreshToken()).isNotEqualTo(refreshToken);
  }

  @Test
  void shouldRevokeUsedTokenOnRotation() {
    String refreshToken = context.tokenPairs.issueFor(userId).refreshToken();

    refresh.execute(refreshToken);

    assertThat(context.refreshTokens.findByTokenHash(OpaqueTokens.hash(refreshToken)))
        .get()
        .extracting(RefreshToken::isRevoked)
        .isEqualTo(true);
  }

  @Test
  void shouldRejectUnknownToken() {
    assertThatThrownBy(() -> refresh.execute("unknown-token"))
        .isInstanceOf(InvalidRefreshTokenException.class);
  }

  @Test
  void shouldRejectExpiredToken() {
    String expired = OpaqueTokens.generate();
    context.refreshTokens.save(
        RefreshToken.issue(
            userId,
            OpaqueTokens.hash(expired),
            NOW.minus(REFRESH_TTL).minusSeconds(1),
            REFRESH_TTL));

    assertThatThrownBy(() -> refresh.execute(expired))
        .isInstanceOf(InvalidRefreshTokenException.class);
  }

  @Test
  void shouldRevokeAllSessionsWhenRotatedTokenIsReused() {
    String stolen = context.tokenPairs.issueFor(userId).refreshToken();
    refresh.execute(stolen);
    context.tokenPairs.issueFor(userId);

    assertThatThrownBy(() -> refresh.execute(stolen))
        .isInstanceOf(InvalidRefreshTokenException.class);
    assertThat(context.refreshTokens.activeOf(userId, NOW)).isEmpty();
  }
}
