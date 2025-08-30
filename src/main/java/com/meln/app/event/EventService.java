package com.meln.app.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EventService {

  private final EventRepo eventRepo;

  public void saveOrUpdate(Collection<EventDto> events) {
    List<Event> eventEntities = events.stream().map(EventConverter::from).toList();
    eventRepo.bulkUpsert(eventEntities);
  }
}
