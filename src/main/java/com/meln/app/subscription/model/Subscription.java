package com.meln.app.subscription.model;

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
@Table(name = "subscription")
public class Subscription {

  public static final String COL_USER_ID = "user_id";
  public static final String COL_IS_ACTIVE = "is_active";
  public static final String SUBJECT_ID = "subject_id";
  public static final String COL_CREATED_AT = "created_at";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = COL_USER_ID, nullable = false)
  private Long userId;

  @Column(name = COL_IS_ACTIVE, nullable = false)
  private boolean active = true;

  @Column(name = SUBJECT_ID, nullable = false)
  private Long subjectId;

  @CreationTimestamp
  @Column(name = COL_CREATED_AT, nullable = false)
  private Instant createdAt;
}
