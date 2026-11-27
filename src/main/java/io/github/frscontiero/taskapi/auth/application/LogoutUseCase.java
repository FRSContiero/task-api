package io.github.frscontiero.taskapi.auth.application;

import io.github.frscontiero.taskapi.auth.domain.OpaqueTokens;
import io.github.frscontiero.taskapi.auth.domain.RefreshTokenRepository;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Idempotent: an unknown, already revoked or someone else's token is ignored, so the response never
 * tells whether a token exists.
 */
@Service
public class LogoutUseCase {

  private final RefreshTokenRepository refreshTokens;
  private final Clock clock;

  public LogoutUseCase(RefreshTokenRepository refreshTokens, Clock clock) {
    this.refreshTokens = refreshTokens;
    this.clock = clock;
  }

  @Transactional
  public void execute(LogoutCommand command) {
    refreshTokens
        .findByTokenHash(OpaqueTokens.hash(command.refreshToken()))
        .filter(token -> token.belongsTo(command.userId()))
        .ifPresent(token -> token.revoke(Instant.now(clock)));
  }
}
