package io.github.frscontiero.taskapi.auth.infrastructure;

import io.github.frscontiero.taskapi.auth.domain.RefreshToken;
import io.github.frscontiero.taskapi.auth.domain.RefreshTokenRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class JpaRefreshTokenRepository implements RefreshTokenRepository {

  private final SpringDataRefreshTokenRepository jpa;

  JpaRefreshTokenRepository(SpringDataRefreshTokenRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public RefreshToken save(RefreshToken token) {
    return jpa.save(token);
  }

  @Override
  public Optional<RefreshToken> findByTokenHash(String tokenHash) {
    return jpa.findByTokenHash(tokenHash);
  }

  @Override
  public void revokeAllActiveOf(UUID userId, Instant revokedAt) {
    jpa.revokeAllActiveOf(userId, revokedAt);
  }
}
