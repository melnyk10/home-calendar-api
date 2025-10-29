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

    var eventTasks = events.stream()
        .flatMap(event -> event.getTargets().stream()
            .map(target -> new EventTargetTask(event, target)))
        .toList();

    var userSubscriptionByTargetId = listUserSubscriptionsByTargetIds(events);

    Map<Long, CalendarClientConnection> calendarClientByUserId = new HashMap<>();

    List<UserEvent> processedUserEvents = new ArrayList<>();
    for (var task : eventTasks) {
      //todo: what if user is subscribed to more than one target in event?
      var usersSubscription = userSubscriptionByTargetId.get(task.target().getId());
      var userEvents = createUsersEvents(usersSubscription, task, calendarClientByUserId);
      processedUserEvents.addAll(userEvents);
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

  private List<UserEvent> createUsersEvents(List<UserSubscription> usersSubscription,
      EventTargetTask task, Map<Long, CalendarClientConnection> calendarClientByUserId) {

    List<UserEvent> processedUserEvents = new ArrayList<>();
    for (var subscription : usersSubscription) {
      UserEvent userEvent;
      try {
        userEvent = createEvent(task, subscription, calendarClientByUserId);
      } catch (Exception exception) {
        log.error("Error creating event for user: {}, event: {}",
            subscription.getUser().getEmail(), task.event().getId(), exception);
        continue;
      }
      processedUserEvents.add(userEvent);
    }

    return processedUserEvents;
  }

  private UserEvent createEvent(EventTargetTask task, UserSubscription subscription,
      Map<Long, CalendarClientConnection> calendarClientByUserId) {
    var userId = subscription.getUser().getId();
    var calendarClientConnection = calendarClientByUserId.computeIfAbsent(userId,
        id -> calendarService.auth(id, task.event().getProvider().getId()));
    var newEventId = calendarClientConnection.createEvent(EventPayload.from(task.event()));
    return buildUserEvent(task.event(), userId, newEventId);
  }

  private UserEvent buildUserEvent(Event event, Long userId, String calendarSourceEventId) {
    UserEvent userEvent = new UserEvent();
    userEvent.setEventId(event.getId());
    userEvent.setHash(event.getHash());
    userEvent.setCalendarSourceEventId(calendarSourceEventId);
    userEvent.setUserId(userId);
    return userEvent;
  }

  public record EventTargetTask(Event event, Target target) {

  }

}
