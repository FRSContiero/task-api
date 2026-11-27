package io.github.frscontiero.taskapi.user.infrastructure;

import io.github.frscontiero.taskapi.user.domain.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class EmailConverter implements AttributeConverter<Email, String> {

  @Override
  public String convertToDatabaseColumn(Email email) {
    return email == null ? null : email.value();
  }

  @Override
  public Email convertToEntityAttribute(String value) {
    return value == null ? null : new Email(value);
  }
}
