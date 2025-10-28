package com.meln.app.calendar;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class CalendarService {

  private final CalendarConnectionRepository calendarConnectionRepository;
  private final CalendarRegistry calendarRegistry;

  public CalendarClient.CalendarClientConnection auth(Long userId, Integer providerId) {
    var calendarConnection =
        calendarConnectionRepository.findAllCalendarConnections(userId, providerId);
    return calendarRegistry.auth(calendarConnection);
  }

}
