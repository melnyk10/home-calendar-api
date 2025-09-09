package com.meln.app.event;

import com.meln.app.event.model.Event;
import com.meln.app.event.model.EventPayload;
import lombok.experimental.UtilityClass;

@UtilityClass
class EventConverter {

  public static EventPayload from(Event event) {
    if (event == null) {
      return null;
    }

    return EventPayload.builder()
        .id(event.getId())
        .sourceId(event.getSourceId())
        .calendarEventSourceId(event.getCalendarEventSourceId())
        .title(event.getTitle())
        .details(event.getDetails())
        .url(event.getUrl())
        .notes(event.getNotes())
        .allDay(event.isAllDay())
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .build();
  }

  public static Event from(EventPayload dto) {
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
