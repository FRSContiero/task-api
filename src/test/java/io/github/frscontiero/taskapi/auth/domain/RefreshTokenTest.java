package io.github.frscontiero.taskapi.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {

  private static final Instant ISSUED_AT = Instant.parse("2026-11-26T10:00:00Z");
  private static final Duration TTL = Duration.ofDays(7);

  private final RefreshToken token = RefreshToken.issue(UUID.randomUUID(), "hash", ISSUED_AT, TTL);

  @Test
  void shouldBeActiveBeforeExpiration() {
    assertThat(token.isActive(ISSUED_AT.plus(TTL).minusSeconds(1))).isTrue();
  }

  @Test
  void shouldExpireExactlyAtExpirationTime() {
    assertThat(token.isActive(ISSUED_AT.plus(TTL))).isFalse();
  }

  @Test
  void shouldNotBeActiveAfterRevocation() {
    token.revoke(ISSUED_AT.plusSeconds(60));

    assertThat(token.isActive(ISSUED_AT.plusSeconds(120))).isFalse();
  }

  @Test
  void shouldKeepFirstRevocationTime() {
    token.revoke(ISSUED_AT.plusSeconds(60));

    token.revoke(ISSUED_AT.plusSeconds(120));

    assertThat(token.getRevokedAt()).isEqualTo(ISSUED_AT.plusSeconds(60));
  }
}
