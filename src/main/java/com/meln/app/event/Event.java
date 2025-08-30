<<<<<<<< HEAD:src/main/java/com/meln/app/event/model/Event.java
package com.meln.app.event.model;
========
package com.meln.app.event;
>>>>>>>> 4626548 (move files):src/main/java/com/meln/app/event/Event.java

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = {"provider", "sourceId"})
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "events")
public class Event extends PanacheMongoEntity {

  public static final String COL_PROVIDER = "provider";
  public static final String COL_SOURCE_ID = "sourceId";
  public static final String COL_TITLE = "title";
  public static final String COL_URL = "url";
  public static final String COL_DETAILS = "details";
  public static final String COL_NOTES = "notes";
  public static final String COL_ALL_DAY = "allDay";
  public static final String COL_START_AT = "startAt";
  public static final String COL_END_AT = "endAt";
  public static final String COL_CREATED_AT = "createdAt";
  public static final String COL_UPDATED_AT = "updatedAt";

  @NotBlank
  @BsonProperty(COL_SOURCE_ID)
  private String sourceId;

  @NotNull
  @BsonProperty(COL_PROVIDER)
  private Provider provider;

  @NotBlank
  @BsonProperty(COL_TITLE)
  private String title;

  @BsonProperty(COL_URL)
  private String url;

  @BsonProperty(COL_DETAILS)
  private String details;

  @BsonProperty(COL_NOTES)
  private String notes;

  @BsonProperty(COL_ALL_DAY)
  private boolean allDay;

  @NotNull
  @BsonProperty(COL_START_AT)
  private Instant startAt;

  @BsonProperty(COL_END_AT)
  private Instant endAt;

  @BsonProperty(COL_CREATED_AT)
  private Instant createdAt;

  @BsonProperty(COL_UPDATED_AT)
  private Instant updatedAt;

  @Override
  public void persist() {
    prePersist();
    super.persist();
  }

  public void prePersist() {
    Instant now = Instant.now();
    if (createdAt == null) {
      createdAt = now;
    }
    updatedAt = now;
  }

  @Override
  public void update() {
    preUpdate();
    super.update();
  }

  public void preUpdate() {
    updatedAt = Instant.now();
  }
}
