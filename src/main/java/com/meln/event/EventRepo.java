package com.meln.event;

import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bson.conversions.Bson;

@ApplicationScoped
public class EventRepo implements PanacheMongoRepository<Event> {

  public void bulkUpsert(Collection<Event> events) {
    List<UpdateOneModel<Event>> writes = events.stream()
        .filter(e -> e.getProvider() != null && e.getSourceId() != null)
        .map(e -> {
          Instant now = Instant.now();

          Bson filter = Filters.and(
              Filters.eq("provider", e.getProvider()),
              Filters.eq("external_id", e.getSourceId())
          );

          List<Bson> sets = new ArrayList<>();
          Optional.ofNullable(e.getTitle()).ifPresent(v -> sets.add(Updates.set("title", v)));
          Optional.ofNullable(e.getUrl()).ifPresent(v -> sets.add(Updates.set("url", v)));
          Optional.ofNullable(e.getNotes()).ifPresent(v -> sets.add(Updates.set("details", v)));
          Optional.ofNullable(e.getStartAt()).ifPresent(v -> sets.add(Updates.set("start_at", v)));
          Optional.ofNullable(e.getEndAt()).ifPresent(v -> sets.add(Updates.set("end_at", v)));
          Optional.ofNullable(e.getSourceId())
              .ifPresent(v -> sets.add(Updates.set("event_source_id", v)));

          sets.add(Updates.set("all_day", e.isAllDay()));
          sets.add(Updates.set("updated_at", now));

          Bson setStage = Updates.combine(sets.toArray(new Bson[0]));

          Bson setOnInsertStage = Updates.combine(
              Updates.setOnInsert("provider", e.getProvider()),
              Updates.setOnInsert("external_id", e.getSourceId()),
              Updates.setOnInsert("created_at", now)
          );

          Bson update = Updates.combine(setStage, setOnInsertStage);

          return new UpdateOneModel<Event>(
              filter,
              update,
              new UpdateOptions().upsert(true)
          );
        })
        .toList();

    if (!writes.isEmpty()) {
      mongoCollection().bulkWrite(writes, new BulkWriteOptions().ordered(false));
    }
  }
}
