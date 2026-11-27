package io.github.frscontiero.taskapi.auth.domain;

import io.github.frscontiero.taskapi.shared.error.BusinessRuleException;

public class IncorrectCurrentPasswordException extends BusinessRuleException {

  public IncorrectCurrentPasswordException() {
    super("Current password is incorrect");
  }
}
