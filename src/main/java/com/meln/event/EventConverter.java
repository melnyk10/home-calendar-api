package com.meln.event;

import com.meln.common.event.EventDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EventConverter {
    public static EventDto from(Event event) {
        if (event == null) {
            return null;
        }

        return EventDto.builder()
                .sourceId(event.getSourceId())
                .provider(event.getProvider())
                .title(event.getTitle())
                .url(event.getUrl())
                .notes(event.getNotes())
                .allDay(event.isAllDay())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .build();
    }

    public static Event from(EventDto dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        event.setSourceId(dto.getSourceId());
        event.setProvider(dto.getProvider());
        event.setTitle(dto.getTitle());
        event.setUrl(dto.getUrl());
        event.setNotes(dto.getNotes());
        event.setAllDay(dto.isAllDay());
        event.setStartAt(dto.getStartAt());
        event.setEndAt(dto.getEndAt());

        return event;
    }
}
