package com.meln.app.event;

import com.meln.app.event.model.EventPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class EventService {

  private final EventRepository eventRepo;

  public void saveOrUpdate(Collection<EventPayload> events) {
    var eventEntities = events.stream().map(EventConverter::from).toList();
    eventRepo.bulkUpsert(eventEntities);
  }
}
