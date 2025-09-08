package com.meln.app.calendar;

import com.meln.app.event.model.EventPayload;
import javax.naming.AuthenticationException;

public interface CalendarClient<P extends CalendarProperties> {

  Class<P> propertiesType();

  CalendarConnection connect(P props) throws AuthenticationException;

  interface CalendarConnection {

    String createEvent(EventPayload event);

    void updateEvent(EventPayload event);
  }
}
