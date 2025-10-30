package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarConnection;
import com.meln.app.calendar.model.CalendarProvider;
import com.meln.app.event.model.EventPayload;

public interface CalendarClient {

  CalendarProvider type();

  CalendarClient.CalendarClientConnection auth(CalendarConnection connection);

  interface CalendarClientConnection {

    String createEvent(EventPayload event);

    void updateEvent(EventPayload event);
  }
}
