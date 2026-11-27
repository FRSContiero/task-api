package io.github.frscontiero.taskapi.auth.application;

import io.github.frscontiero.taskapi.auth.domain.RefreshToken;
import io.github.frscontiero.taskapi.auth.domain.RefreshTokenRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

  private final List<RefreshToken> tokens = new ArrayList<>();

  @Override
  public RefreshToken save(RefreshToken token) {
    tokens.add(token);
    return token;
  }

  @Override
  public Optional<RefreshToken> findByTokenHash(String tokenHash) {
    return tokens.stream().filter(token -> token.getTokenHash().equals(tokenHash)).findFirst();
  }

  @Override
  public void revokeAllActiveOf(UUID userId, Instant revokedAt) {
    tokens.stream().filter(token -> token.belongsTo(userId)).forEach(t -> t.revoke(revokedAt));
  }

  List<RefreshToken> activeOf(UUID userId, Instant now) {
    return tokens.stream().filter(token -> token.belongsTo(userId) && token.isActive(now)).toList();
  }
}
