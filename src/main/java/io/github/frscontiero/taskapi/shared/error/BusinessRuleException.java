package io.github.frscontiero.taskapi.shared.error;

/** Base for domain exceptions when a business rule is violated (HTTP 422). */
public abstract class BusinessRuleException extends RuntimeException {

  protected BusinessRuleException(String message) {
    super(message);
  }
}
