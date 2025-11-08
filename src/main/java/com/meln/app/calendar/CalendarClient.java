package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarConnection;
import com.meln.app.calendar.model.CalendarProvider;
import com.meln.app.event.model.EventPayload;
import java.util.List;

public interface CalendarClient {

  CalendarProvider type();

  CalendarEventClient auth(CalendarConnection connection);

  interface CalendarEventClient {

    //todo: move to another interface ?
    List<CalendarPayload> listCalendars();

    String createEvent(String calendarId, EventPayload event);

    void updateEvent(String calendarId, EventPayload event);
  }
}
