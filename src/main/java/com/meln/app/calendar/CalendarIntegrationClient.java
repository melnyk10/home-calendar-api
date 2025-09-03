package com.meln.app.calendar;

import com.meln.app.event.model.EventDto;

public interface CalendarIntegrationClient<P extends CalendarIntegrationProperties> {

  Class<P> propertiesType();

  String createEvent(P props, EventDto event);

  void updateEvent(P props, String eventId, EventDto event);
}
