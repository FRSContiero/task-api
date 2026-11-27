package io.github.frscontiero.taskapi.auth.domain;

import io.github.frscontiero.taskapi.shared.error.UnauthorizedException;

public class InvalidRefreshTokenException extends UnauthorizedException {

  public InvalidRefreshTokenException() {
    super("Refresh token is invalid, expired or revoked");
  }
}
