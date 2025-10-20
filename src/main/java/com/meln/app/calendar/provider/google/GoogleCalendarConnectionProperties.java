package com.meln.app.calendar.provider.google;

import com.meln.app.calendar.CalendarConnectionProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class GoogleCalendarConnectionProperties implements CalendarConnectionProperties {

  String userEmail;
  String calendarId;
}
