package com.meln.app.calendar;

import com.meln.app.calendar.CalendarClient.CalendarEventClient;
import com.meln.app.calendar.model.Calendar;
import com.meln.app.common.error.ErrorMessage.Calendar.Code;
import com.meln.app.common.error.ErrorMessage.Calendar.Message;
import com.meln.app.common.error.ServerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class CalendarService {

  private final CalendarRepository calendarRepository;
  private final CalendarConnectionRepository calendarConnectionRepository;
  private final CalendarRegistry calendarRegistry;

  public CalendarEventClient auth(String email, Integer providerId) {
    var calendarConnection =
        calendarConnectionRepository.findAllCalendarConnections(email, providerId);
    if (calendarConnection == null) {
      return null;
    }
    return calendarRegistry.auth(calendarConnection);
  }

  public List<Calendar> list(String email) {
    return calendarRepository.findByEmail(email);
  }

  public void delete(String email, Collection<Integer> calendarIds) {
    calendarRepository.deleteCalendars(email, calendarIds);
  }

  @Transactional
  public void sync(String email) {
    var calendarConnection = calendarConnectionRepository.findByUserEmail(email);
    if (calendarConnection.isEmpty()) {
      log.debug("No calendar connection found for email {}", email);
      return;
    }

    var calendarClient = calendarRegistry.auth(calendarConnection.get());
    var calendarsResponse = calendarClient.listCalendars();
    var calendarEntities = calendarsResponse.stream()
        .map(it -> Calendar.builder()
            .sourceCalendarId(it.id())
            .email(it.email())
            .name(it.name())
            .provider(it.provider())
            .build())
        .toList();

    try {
      calendarRepository.persist(calendarEntities);
    } catch (Exception e) {
      log.error("Can't save synced calendars. Error message: {}", e.getMessage());
      throw new ServerException(Code.CANNOT_SAVE_SYNCED_CALENDARS,
          Message.CANNOT_SAVE_SYNCED_CALENDARS);
    }
  }

}
