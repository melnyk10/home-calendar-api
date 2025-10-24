package com.meln.app.user;

import com.meln.app.user.model.UserCalendarEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class UserCalendarEventService {

  private final UserCalendarEventRepository userCalendarEventRepository;

  public void create(List<UserCalendarEvent> userCalendarEvents) {
    Instant now = Instant.now();
    var userEvents = userCalendarEvents.stream()
        .map(it -> UserCalendarEvent.builder()
            .userCalendarId(null) //todo: where to get userId and how to define the calendar ?
//            .eventId(it.eventId())
//            .sourceEventId(it.sourceEventId())
            .createdAt(now)
            .updatedAt(now)
            .build())
        .toList();
    userCalendarEventRepository.saveAll(userEvents);
  }

}
