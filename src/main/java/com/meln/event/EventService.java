package com.meln.event;

import com.meln.common.event.EventClient;
import com.meln.common.event.EventDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EventService implements EventClient {

  private final EventRepo eventRepo;

  @Override
  public void saveOrUpdate(Collection<EventDto> events) {
    List<Event> eventEntities = events.stream().map(EventConverter::from).toList();
    eventRepo.bulkUpsert(eventEntities);
  }
}
