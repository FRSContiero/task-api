package io.github.frscontiero.taskapi.user.domain;

import io.github.frscontiero.taskapi.shared.error.ConflictException;

public class EmailAlreadyRegisteredException extends ConflictException {

  public EmailAlreadyRegisteredException() {
    super("Email is already registered");
  }
}
