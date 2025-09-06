package com.meln.app.calendar;

import com.meln.app.event.model.EventDto;
import javax.naming.AuthenticationException;

public interface CalendarClient<P extends CalendarProperties> {

  Class<P> propertiesType();

  CalendarConnection connect(P props) throws AuthenticationException;

  interface CalendarConnection extends AutoCloseable {

    String createEvent(EventDto event);

    void updateEvent(EventDto event);

    @Override
    default void close() {
    }
  }
}
