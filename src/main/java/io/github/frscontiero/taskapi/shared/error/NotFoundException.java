package io.github.frscontiero.taskapi.shared.error;

/** Base for domain exceptions when a requested resource does not exist (HTTP 404). */
public abstract class NotFoundException extends RuntimeException {

  protected NotFoundException(String message) {
    super(message);
  }
}
