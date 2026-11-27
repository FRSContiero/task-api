package io.github.frscontiero.taskapi.auth.domain;

import io.github.frscontiero.taskapi.shared.error.UnauthorizedException;

/** Same message for unknown email and wrong password, so the API does not reveal which. */
public class InvalidCredentialsException extends UnauthorizedException {

  public InvalidCredentialsException() {
    super("Invalid email or password");
  }
}
