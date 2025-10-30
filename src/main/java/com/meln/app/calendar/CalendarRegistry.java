package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarConnection;
import com.meln.app.calendar.model.CalendarProvider;
import com.meln.app.common.error.ErrorMessage;
import com.meln.app.common.error.ServerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CalendarRegistry {

  private final Map<CalendarProvider, CalendarClient> calendarClientByProvider = new HashMap<>();

  @Inject
  public CalendarRegistry(Instance<CalendarClient> calendarClients) {
    calendarClients.forEach(c -> calendarClientByProvider.put(c.type(), c));
  }

  public CalendarClient.CalendarClientConnection auth(CalendarConnection connection) {
    if (connection == null) {
      throw new ServerException(
          ErrorMessage.Calendar.Code.INVALID_CALENDAR_PROVIDER_PROPERTIES,
          ErrorMessage.Calendar.Message.CALENDAR_PROVIDER_PROPERTIES_NOT_PROVIDED);
    }

    CalendarProvider google = CalendarProvider.GOOGLE;
    var calendarClient = calendarClientByProvider.get(google);
    if (calendarClient == null) {
      throw new ServerException(ErrorMessage.Calendar.Code.INVALID_CALENDAR_PROVIDER,
          ErrorMessage.Calendar.Message.NO_PROVIDER_BY_PROPS(google.name()));
    }
    return calendarClient.auth(connection);
  }
}
