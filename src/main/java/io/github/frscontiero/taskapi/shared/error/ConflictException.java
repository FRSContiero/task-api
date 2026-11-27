package io.github.frscontiero.taskapi.shared.error;

/** Base for domain exceptions when an operation conflicts with the current state (HTTP 409). */
public abstract class ConflictException extends RuntimeException {

  protected ConflictException(String message) {
    super(message);
  }
}
