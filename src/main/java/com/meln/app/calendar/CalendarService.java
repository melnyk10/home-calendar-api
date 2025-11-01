package com.meln.app.calendar;

import com.meln.app.calendar.model.Calendar;
import com.meln.app.calendar.model.CalendarProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class CalendarService {

  private final CalendarRepository calendarRepository;
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

  @Transactional
  public Calendar getByEmailAndProvider(String email, CalendarProvider provider) {
    return calendarRepository.findByEmailAndProvider(email, provider)
        .orElseThrow(EntityNotFoundException::new);
  }

}
