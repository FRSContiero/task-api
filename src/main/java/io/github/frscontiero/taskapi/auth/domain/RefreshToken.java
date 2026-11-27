package io.github.frscontiero.taskapi.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/** Server-side record of an opaque refresh token. Only its SHA-256 hash is stored. */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

  @Id private UUID id;

  @Column(name = "user_id", nullable = false, updatable = false)
  private UUID userId;

  @Column(name = "token_hash", nullable = false, updatable = false, length = 64)
  private String tokenHash;

  @Column(name = "expires_at", nullable = false, updatable = false)
  private Instant expiresAt;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  // Guards against two concurrent refreshes rotating the same token
  @Version private Long version;

  protected RefreshToken() {}

  private RefreshToken(UUID userId, String tokenHash, Instant createdAt, Duration ttl) {
    this.id = UUID.randomUUID();
    this.userId = userId;
    this.tokenHash = tokenHash;
    this.createdAt = createdAt;
    this.expiresAt = createdAt.plus(ttl);
  }

  public static RefreshToken issue(UUID userId, String tokenHash, Instant now, Duration ttl) {
    return new RefreshToken(userId, tokenHash, now, ttl);
  }

  public boolean isRevoked() {
    return revokedAt != null;
  }

  public boolean isExpired(Instant now) {
    return !now.isBefore(expiresAt);
  }

  public boolean isActive(Instant now) {
    return !isRevoked() && !isExpired(now);
  }

  public boolean belongsTo(UUID userId) {
    return this.userId.equals(userId);
  }

  public void revoke(Instant now) {
    if (isRevoked()) {
      return;
    }
    this.revokedAt = now;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getTokenHash() {
    return tokenHash;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public Instant getRevokedAt() {
    return revokedAt;
  }
}
