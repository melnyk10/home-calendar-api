package com.meln.app.event.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "event",
    indexes = {
        @Index(name = "idx_event_subject_time", columnList = "subject_id, occurred_at"),
        @Index(name = "idx_event_type_time", columnList = "type, occurred_at"),
        @Index(name = "idx_event_provider_time", columnList = "provider_id, occurred_at")
    }
)
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "uuid")
  private Long id;

  @Column(name = "source_id", nullable = false, unique = true)
  private String sourceId;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "provider_id", nullable = false)
  private Provider provider;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TargetType type;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "details", nullable = false)
  private String details;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb", nullable = false)
  private Map<String, Object> payload = new HashMap<>();

  @ManyToMany
  @JoinTable(
      name = "event_target",
      joinColumns = @JoinColumn(name = "event_id"),
      inverseJoinColumns = @JoinColumn(name = "target_id")
  )
  private Set<Target> targets = new HashSet<>();

  @Generated
  @Column(name = "hash", nullable = false, length = 32, insertable = false, updatable = false)
  private String hash;

  @Column(name = "is_all_day", nullable = false)
  private boolean allDay;

  @Column(name = "start_at", nullable = false)
  private Instant startAt;

  @Column(name = "end_at", nullable = false)
  private Instant endAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
