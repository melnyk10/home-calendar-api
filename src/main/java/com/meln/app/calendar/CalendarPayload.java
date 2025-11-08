package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarProvider;
import lombok.Builder;

@Builder
public record CalendarPayload(String id, String name, String email, CalendarProvider provider) {

}
