package com.meln.app.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
  public static final String COL_EMAIL = "email";
  public static final String COL_FIRST_NAME = "first_name";
  public static final String COL_LAST_NAME = "last_name";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = COL_FIRST_NAME, nullable = false)
  private String firstName;

  @Column(name = COL_LAST_NAME, nullable = false)
  private String lastName;

  @Column(name = COL_EMAIL, nullable = false, unique = true)
  private String email;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
