package io.github.frscontiero.taskapi.auth.application;

import io.github.frscontiero.taskapi.auth.domain.AccessToken;
import io.github.frscontiero.taskapi.auth.domain.AccessTokenIssuer;
import io.github.frscontiero.taskapi.user.application.FakePasswordHasher;
import io.github.frscontiero.taskapi.user.application.InMemoryUserRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

/** Fakes shared by the auth use case tests. */
class AuthTestContext {

  static final Instant NOW = Instant.parse("2026-11-26T10:00:00Z");
  static final Duration REFRESH_TTL = Duration.ofDays(7);

  final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
  final InMemoryUserRepository users = new InMemoryUserRepository();
  final InMemoryRefreshTokenRepository refreshTokens = new InMemoryRefreshTokenRepository();
  final FakePasswordHasher passwordHasher = new FakePasswordHasher();
  final AccessTokenIssuer accessTokens =
      userId -> new AccessToken("access-" + userId, Duration.ofMinutes(15));
  final TokenPairIssuer tokenPairs =
      new TokenPairIssuer(accessTokens, refreshTokens, clock, REFRESH_TTL);
}
