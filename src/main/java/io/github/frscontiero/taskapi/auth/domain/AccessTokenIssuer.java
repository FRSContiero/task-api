package io.github.frscontiero.taskapi.auth.domain;

import java.util.UUID;

public interface AccessTokenIssuer {

  AccessToken issueFor(UUID userId);
}
