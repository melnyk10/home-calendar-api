package com.meln.app.calendar;

import com.meln.app.event.model.EventDto;
import javax.naming.AuthenticationException;

public interface CalendarClient<P extends CalendarProperties> {

  Class<P> propertiesType();

  void connect(P props) throws AuthenticationException;

  String createEvent(EventDto event);

  void updateEvent(String eventId, EventDto event);
}
