package com.meln.app.event;

import com.meln.app.event.model.EventDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class EventService {

  private final EventRepo eventRepo;

  public void saveOrUpdate(Collection<EventDto> events) {
    var eventEntities = events.stream().map(EventConverter::from).toList();
    eventRepo.bulkUpsert(eventEntities);
  }
}
