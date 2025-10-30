package com.meln.app.calendar;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class CalendarService {

  private final CalendarConnectionRepository calendarConnectionRepository;
  private final CalendarRegistry calendarRegistry;

  public CalendarClient.CalendarClientConnection auth(String email, Integer providerId) {
    var calendarConnection =
        calendarConnectionRepository.findAllCalendarConnections(email, providerId);
    if (calendarConnection == null) {
      return null;
    }
    return calendarRegistry.auth(calendarConnection);
  }

}
