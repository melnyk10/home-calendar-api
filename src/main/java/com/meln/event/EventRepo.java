package com.meln.event;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;

import java.time.Instant;
import java.util.Collection;

@ApplicationScoped
public class EventRepo implements PanacheMongoRepository<Event> {

    public void upsertAll(Collection<Event> events) {
        events.forEach(this::upsert);
    }

    public void upsert(Event e) {
        var filter = Filters.and(
                Filters.eq("provider", e.getProvider()),
                Filters.eq("external_id", e.getExternalId())
        );

        Instant now = Instant.now();
        if (e.getCreatedAt() == null) e.setCreatedAt(now);
        e.setUpdatedAt(now);

        var set = new Document()
                .append("title", e.getTitle())
                .append("url", e.getUrl())
                .append("details", e.getDetails())
                .append("start_at", e.getStartAt())
                .append("end_at", e.getEndAt())
                .append("all_day", e.isAllDay())
                .append("event_source_id", e.getEventSourceId())
                .append("subscription_id", e.getSubscriptionId())
                .append("updated_at", e.getUpdatedAt());

        var setOnInsert = new Document()
                .append("provider", e.getProvider())
                .append("external_id", e.getExternalId())
                .append("created_at", e.getCreatedAt());

        var update = new Document("$set", set).append("$setOnInsert", setOnInsert);

        mongoCollection().updateOne(filter, update, new UpdateOptions().upsert(true));
    }

}
