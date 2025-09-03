package com.meln.app.event;

import com.meln.app.event.model.Event;
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
public class EventRepository implements PanacheMongoRepository<Event> {

  public void bulkUpsert(Collection<Event> events) {
    List<UpdateOneModel<Event>> writes = events.stream()
        .filter(e -> e.getProvider() != null && e.getSourceId() != null)
        .map(e -> {
          Instant now = Instant.now();

          Bson filter = Filters.and(
              Filters.eq(Event.COL_PROVIDER, e.getProvider()),
              Filters.eq(Event.COL_SOURCE_ID, e.getSourceId())
          );

          List<Bson> sets = new ArrayList<>();
          Optional.ofNullable(e.getCalendarEventSourceId())
              .ifPresent(v -> sets.add(Updates.set(Event.COL_CALENDAR_EVENT_SOURCE_ID, v)));
          Optional.ofNullable(e.getTitle())
              .ifPresent(v -> sets.add(Updates.set(Event.COL_TITLE, v)));
          Optional.ofNullable(e.getUrl()).ifPresent(v -> sets.add(Updates.set(Event.COL_URL, v)));
          Optional.ofNullable(e.getNotes())
              .ifPresent(v -> sets.add(Updates.set(Event.COL_DETAILS, v)));
          Optional.ofNullable(e.getStartAt())
              .ifPresent(v -> sets.add(Updates.set(Event.COL_START_AT, v)));
          Optional.ofNullable(e.getEndAt())
              .ifPresent(v -> sets.add(Updates.set(Event.COL_END_AT, v)));

          sets.add(Updates.set(Event.COL_ALL_DAY, e.isAllDay()));
          sets.add(Updates.set(Event.COL_UPDATED_AT, now));

          Bson setStage = Updates.combine(sets.toArray(new Bson[0]));

          Bson setOnInsertStage = Updates.combine(
              Updates.setOnInsert(Event.COL_PROVIDER, e.getProvider()),
              Updates.setOnInsert(Event.COL_SOURCE_ID, e.getSourceId()),
              Updates.setOnInsert(Event.COL_CREATED_AT, now)
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
