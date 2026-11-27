package io.github.frscontiero.taskapi.auth.application;

import io.github.frscontiero.taskapi.auth.domain.InvalidRefreshTokenException;
import io.github.frscontiero.taskapi.auth.domain.OpaqueTokens;
import io.github.frscontiero.taskapi.auth.domain.RefreshToken;
import io.github.frscontiero.taskapi.auth.domain.RefreshTokenRepository;
import java.time.Clock;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokensUseCase {

  private static final Logger log = LoggerFactory.getLogger(RefreshTokensUseCase.class);

  private final RefreshTokenRepository refreshTokens;
  private final TokenPairIssuer tokenPairs;
  private final Clock clock;

  RefreshTokensUseCase(
      RefreshTokenRepository refreshTokens, TokenPairIssuer tokenPairs, Clock clock) {
    this.refreshTokens = refreshTokens;
    this.tokenPairs = tokenPairs;
    this.clock = clock;
  }

  // The revocation done on reuse must be committed even though the request fails
  @Transactional(noRollbackFor = InvalidRefreshTokenException.class)
  public TokenPair execute(String refreshToken) {
    Instant now = Instant.now(clock);
    RefreshToken current =
        refreshTokens
            .findByTokenHash(OpaqueTokens.hash(refreshToken))
            .orElseThrow(InvalidRefreshTokenException::new);

    if (current.isRevoked()) {
      // A rotated token coming back means it leaked: end every session of this user
      log.warn("Refresh token reuse detected for user {}", current.getUserId());
      refreshTokens.revokeAllActiveOf(current.getUserId(), now);
      throw new InvalidRefreshTokenException();
    }
    if (current.isExpired(now)) {
      throw new InvalidRefreshTokenException();
    }

    current.revoke(now);
    return tokenPairs.issueFor(current.getUserId());
  }
}
