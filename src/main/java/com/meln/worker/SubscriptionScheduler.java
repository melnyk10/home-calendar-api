package com.meln.worker;

import com.meln.app.calendar.CalendarService;
import com.meln.app.event.EventService;
import com.meln.app.event.model.Event;
import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.Target;
import com.meln.app.user.UserEventService;
import com.meln.app.user.UserSubscriptionService;
import com.meln.app.user.model.UserEvent;
import com.meln.app.user.model.UserSubscription;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionScheduler {

  private final EventService eventService;
  private final UserEventService userEventService;
  private final UserSubscriptionService userSubscriptionService;
  private final CalendarService calendarService;

  //todo: find better way to update events. Something like webhook, Queue?
  @Scheduled(every = "20m")
  public void sync() {
    createUserEvents();
    //todo: updateUserEvents(calendarByUserId);
  }

  private void createUserEvents() {
    var events = eventService.findAllByNotFoundEvents();
    if (events.isEmpty()) {
      log.info("No events found for creation");
      return;
    }

    Set<Long> eventTargetIds = events.stream()
        .flatMap(e -> e.getTargets().stream())
        .map(Target::getId)
        .collect(Collectors.toSet());

    Map<Long, List<UserSubscription>> userSubscriptionByTargetId =
        userSubscriptionService.listUserSubscriptionMap(eventTargetIds);

    List<UserEvent> processedUserEvents = new ArrayList<>();
    for (Event event : events) {
      for (Target target : event.getTargets()) {
        var userSubscription = userSubscriptionByTargetId.get(target.getId());
        for (UserSubscription subscription : userSubscription) {
          Long userId = subscription.getUser().getId();
          var calendarClient = calendarService.auth(userId, event.getProvider().getId());
          String newEventId = calendarClient.createEvent(EventPayload.from(event));

          var userEvent = buildUserEvent(event, userId, newEventId);
          processedUserEvents.add(userEvent);
        }
      }
    }
    userEventService.saveAll(processedUserEvents);
  }

  private UserEvent buildUserEvent(Event event, Long userId, String calendarSourceEventId) {
    UserEvent userEvent = new UserEvent();
    userEvent.setEventId(event.getId());
    userEvent.setHash(event.getHash());
    userEvent.setCalendarSourceEventId(calendarSourceEventId);
    userEvent.setUserId(userId);
    return userEvent;
  }

}
