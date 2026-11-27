package io.github.frscontiero.taskapi.auth.infrastructure;

import io.github.frscontiero.taskapi.auth.domain.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

  Optional<RefreshToken> findByTokenHash(String tokenHash);

  // Bumps the version so a refresh racing with this revocation fails instead of rotating
  @Modifying(flushAutomatically = true)
  @Query(
      """
      update RefreshToken t
         set t.revokedAt = :revokedAt, t.version = t.version + 1
       where t.userId = :userId and t.revokedAt is null
      """)
  int revokeAllActiveOf(@Param("userId") UUID userId, @Param("revokedAt") Instant revokedAt);
}
