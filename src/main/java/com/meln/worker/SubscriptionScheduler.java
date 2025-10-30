package com.meln.worker;

import com.meln.app.calendar.CalendarClient.CalendarClientConnection;
import com.meln.app.calendar.CalendarService;
import com.meln.app.event.EventService;
import com.meln.app.event.model.Event;
import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.Target;
import com.meln.app.user.UserEventRepository;
import com.meln.app.user.UserSubscriptionService;
import com.meln.app.user.model.UserEvent;
import com.meln.app.user.model.UserSubscription;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionScheduler {

  private final EventService eventService;
  private final UserEventRepository userEventRepository;
  private final UserSubscriptionService userSubscriptionService;
  private final CalendarService calendarService;

  //todo: find better way to update events. Something like queue?
  @Scheduled(every = "20m")
  public void sync() {
    Map<String, CalendarClientConnection> calendarClientByUserId = new HashMap<>();
    createUserEvents(calendarClientByUserId);
    updateUserEvents(calendarClientByUserId);
  }

  private void createUserEvents(Map<String, CalendarClientConnection> calendarClientByUserId) {
    var events = eventService.listMissingUserEvents();
    if (events.isEmpty()) {
      log.info("No events found for creation");
      return;
    }

    var eventTasks = events.stream()
        .flatMap(event -> event.getTargets().stream()
            .map(target -> new EventTargetTask(event, target)))
        .toList();

    var subscriptionByTargetId = listUserSubscriptionsByTargetIds(events);

    List<UserEvent> processedUserEvents = new ArrayList<>();
    for (var task : eventTasks) {
      //todo: what if user is subscribed to more than one target in event?
      var usersSubscription = subscriptionByTargetId.getOrDefault(task.target().getId(), List.of());
      for (var subscription : usersSubscription) {
        try {
          var event = createEvent(task, subscription, calendarClientByUserId);
          processedUserEvents.add(event);
        } catch (Exception exception) {
          log.error("Can't create event for user: {}, event: {}",
              subscription.getUser().getEmail(), task.event().getId());
        }
      }
    }

    userEventRepository.saveAll(processedUserEvents);
  }

  private Map<Long, List<UserSubscription>> listUserSubscriptionsByTargetIds(List<Event> events) {
    var eventTargetIds = events.stream()
        .flatMap(e -> e.getTargets().stream())
        .map(Target::getId)
        .collect(Collectors.toSet());

    return userSubscriptionService.listUserSubscriptionMap(eventTargetIds);
  }

  private UserEvent createEvent(EventTargetTask task, UserSubscription subscription,
      Map<String, CalendarClientConnection> calendarClientByUserId) {
    var user = subscription.getUser();
    var calendarClientConnection = calendarClientByUserId.computeIfAbsent(user.getEmail(),
        id -> calendarService.auth(user.getEmail(), task.event().getProvider().getId()));
    var newEventId = calendarClientConnection.createEvent(EventPayload.from(task.event()));
    return buildUserEvent(task.event(), user.getId(), newEventId);
  }

  private UserEvent buildUserEvent(Event event, Long userId, String calendarSourceEventId) {
    var userEvent = new UserEvent();
    userEvent.setEventId(event.getId());
    userEvent.setHash(event.getHash());
    userEvent.setCalendarSourceEventId(calendarSourceEventId);
    userEvent.setUserId(userId);
    return userEvent;
  }

  private void updateUserEvents(Map<String, CalendarClientConnection> calendarClientByUserId) {
    var events = eventService.listChangedUserEvents();
    if (events.isEmpty()) {
      log.info("No events found for updating");
      return;
    }

    var eventTasks = events.stream()
        .flatMap(event -> event.getTargets().stream()
            .map(target -> new EventTargetTask(event, target)))
        .toList();

    var subscriptionByTargetId = listUserSubscriptionsByTargetIds(events);

    for (var task : eventTasks) {
      var usersSubscription = subscriptionByTargetId.getOrDefault(task.target().getId(), List.of());
      for (var subscription : usersSubscription) {
        updateEvent(task, subscription, calendarClientByUserId);
      }
    }
  }

  private void updateEvent(EventTargetTask task, UserSubscription subscription,
      Map<String, CalendarClientConnection> calendarClientByUserId) {
    try {
      var user = subscription.getUser();
      var calendarClientConnection = calendarClientByUserId.computeIfAbsent(user.getEmail(),
          id -> calendarService.auth(user.getEmail(), task.event().getProvider().getId()));
      calendarClientConnection.updateEvent(EventPayload.from(task.event()));
      //todo: update hash in UserEvent
    } catch (Exception exception) {
      log.error("Error updating event for user: {}, event: {}",
          subscription.getUser().getEmail(), task.event().getId(), exception);
    }
  }

  public record EventTargetTask(Event event, Target target) {

  }

}
