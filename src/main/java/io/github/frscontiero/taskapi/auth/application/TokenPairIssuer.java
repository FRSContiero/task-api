package io.github.frscontiero.taskapi.auth.application;

import io.github.frscontiero.taskapi.auth.domain.AccessTokenIssuer;
import io.github.frscontiero.taskapi.auth.domain.OpaqueTokens;
import io.github.frscontiero.taskapi.auth.domain.RefreshToken;
import io.github.frscontiero.taskapi.auth.domain.RefreshTokenRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Starts a session: shared by login and refresh, which both end with a new token pair. */
@Component
class TokenPairIssuer {

  private final AccessTokenIssuer accessTokens;
  private final RefreshTokenRepository refreshTokens;
  private final Clock clock;
  private final Duration refreshTokenTtl;

  TokenPairIssuer(
      AccessTokenIssuer accessTokens,
      RefreshTokenRepository refreshTokens,
      Clock clock,
      @Value("${app.jwt.refresh-token-ttl}") Duration refreshTokenTtl) {
    this.accessTokens = accessTokens;
    this.refreshTokens = refreshTokens;
    this.clock = clock;
    this.refreshTokenTtl = refreshTokenTtl;
  }

  TokenPair issueFor(UUID userId) {
    String refreshToken = OpaqueTokens.generate();
    refreshTokens.save(
        RefreshToken.issue(
            userId, OpaqueTokens.hash(refreshToken), Instant.now(clock), refreshTokenTtl));
    return new TokenPair(accessTokens.issueFor(userId), refreshToken);
  }
}
