package com.meln.app.user;

import com.meln.app.user.model.UserEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class UserEventService {

  private final UserEventRepository userEventRepository;

  public void create(List<UserEvent> userCalendarEvents) {
    Instant now = Instant.now();
    var userEvents = userCalendarEvents.stream()
        .map(it -> UserEvent.builder()
//            .userCalendarId(null) //todo: where to get userId and how to define the calendar ?
//            .eventId(it.eventId())
//            .sourceEventId(it.sourceEventId())
            .build())
        .toList();
    userEventRepository.saveAll(userEvents);
  }

  public void saveAll(List<UserEvent> userEvents) {
    userEventRepository.saveAll(userEvents);
  }
}
