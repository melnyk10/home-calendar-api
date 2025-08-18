package com.meln.event;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"provider", "sourceId"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@MongoEntity(collection = "events")
public class Event extends PanacheMongoEntity {
    @NotBlank
    @BsonProperty("source_id")
    private String sourceId;

    @NotNull
    @BsonProperty("provider")
    private Provider provider;

    @BsonProperty("title")
    @NotBlank
    private String title;

    @BsonProperty("url")
    private String url;

    @BsonProperty("notes")
    private String notes;

    @BsonProperty("all_day")
    private boolean allDay;

    @NotNull
    @BsonProperty("start_at")
    private Instant startAt;

    @BsonProperty("end_at")
    private Instant endAt;

    @BsonProperty("created_at")
    private Instant createdAt;

    @BsonProperty("updated_at")
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
