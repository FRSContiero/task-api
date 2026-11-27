package io.github.frscontiero.taskapi.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

  @Id private UUID id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 254)
  private Email email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  // While null it also tells Spring Data the entity is new, since the id is assigned up front
  @Version private Long version;

  protected User() {}

  private User(String name, Email email, String passwordHash, Instant createdAt) {
    this.id = UUID.randomUUID();
    this.name = name.strip();
    this.email = email;
    this.passwordHash = passwordHash;
    this.createdAt = createdAt;
  }

  public static User register(String name, Email email, String passwordHash, Instant createdAt) {
    return new User(name, email, passwordHash, createdAt);
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Email getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
