package com.meln.worker;

import com.meln.app.calendar.CalendarClient;
import com.meln.app.calendar.CalendarClient.CalendarConnection;
import com.meln.app.event.EventService;
import com.meln.app.event.model.Event;
import com.meln.app.event.model.EventPayload;
import com.meln.app.user.UserCalendarEventService;
import com.meln.app.user.UserCalendarRepository;
import com.meln.app.user.model.UserCalendarEventId;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionScheduler {

  private final EventService eventService;
  private final UserCalendarRepository userCalendarRepository;
  private final UserCalendarEventService userCalendarEventService;

  //todo: find better way to update events. Something like webhook
  @Scheduled(every = "20m")
  private void sync() {
    var calendarByUserId = new HashMap<String, CalendarClient.CalendarConnection>();
    createUserEvents(calendarByUserId);
    updateUserEvents(calendarByUserId);
  }

  private void createUserEvents(Map<String, CalendarConnection> calendarByUserId) {
    var events = eventService.findAllByNotFoundEvents();
    if (events.isEmpty()) {
      log.info("No events found for creation");
      return;
    }

    //todo: find calendar by user and event type and pass ?

    var newUserEvents = events.stream()
        .map(it -> new UserCalendarEventId(it.getId(), it.getSourceId()))
        .toList();
    userCalendarEventService.create(newUserEvents);
  }

  private void updateUserEvents(Map<String, CalendarConnection> calendarByUserId) {
    var eventById = eventService.listAllChangedEvents().stream()
        .collect(Collectors.toMap(Event::getId, Function.identity()));
    if (eventById.isEmpty()) {
      log.info("No events found for update");
      return;
    }
    //todo: filter events what are changed. Maybe to use something like eTag/MD5 hash

    var userCalendarEvents = userCalendarRepository.findAllByEventIds(eventById.keySet());
    for (var userCalendarEvent : userCalendarEvents) {
      var event = eventById.get(userCalendarEvent.getEventId());
      var calendarClient = calendarByUserId.get(null);
      calendarClient.updateEvent(EventPayload.toPayload(event));
    }
  }

}
