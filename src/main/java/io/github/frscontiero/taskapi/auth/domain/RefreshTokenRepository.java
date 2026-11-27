package io.github.frscontiero.taskapi.auth.domain;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {

  RefreshToken save(RefreshToken token);

  Optional<RefreshToken> findByTokenHash(String tokenHash);

  void revokeAllActiveOf(UUID userId, Instant revokedAt);
}
