package io.github.frscontiero.taskapi.auth.infrastructure;

import io.github.frscontiero.taskapi.auth.domain.AccessToken;
import io.github.frscontiero.taskapi.auth.domain.AccessTokenIssuer;
import io.github.frscontiero.taskapi.shared.config.JwtProperties;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
class JwtAccessTokenIssuer implements AccessTokenIssuer {

  private final JwtEncoder encoder;
  private final JwtProperties properties;
  private final Clock clock;

  JwtAccessTokenIssuer(JwtEncoder encoder, JwtProperties properties, Clock clock) {
    this.encoder = encoder;
    this.properties = properties;
    this.clock = clock;
  }

  @Override
  public AccessToken issueFor(UUID userId) {
    Instant now = Instant.now(clock);
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiresAt(now.plus(properties.accessTokenTtl()))
            .build();
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    String value = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    return new AccessToken(value, properties.accessTokenTtl());
  }
}
