package com.meln.app.event;

import com.meln.app.event.model.Event;
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
    var eventEntities = events.stream().map(this::from).toList();
    eventRepo.bulkUpsert(eventEntities);
  }

  public Event from(EventPayload dto) {
    if (dto == null) {
      return null;
    }

    return Event.builder()
        .sourceId(dto.getSourceId())
        .calendarEventSourceId(dto.getCalendarEventSourceId())
        .title(dto.getTitle())
        .details(dto.getDetails())
        .url(dto.getUrl())
        .notes(dto.getNotes())
        .allDay(dto.isAllDay())
        .startAt(dto.getStartAt())
        .endAt(dto.getEndAt())
        .build();
  }
}
