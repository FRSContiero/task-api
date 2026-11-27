package io.github.frscontiero.taskapi.shared.error;

/** Base for domain exceptions when credentials or tokens are not accepted (HTTP 401). */
public abstract class UnauthorizedException extends RuntimeException {

  protected UnauthorizedException(String message) {
    super(message);
  }
}
