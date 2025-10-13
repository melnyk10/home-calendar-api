package com.meln.worker;

import com.meln.app.calendar.CalendarConnectionProperties;
import com.meln.app.calendar.CalendarProviderRegistry;
import com.meln.app.calendar.CalendarService;
import com.meln.app.event.EventProviderRegistry;
import com.meln.app.event.EventService;
import com.meln.app.event.model.EventPayload;
import com.meln.app.subscription.SubscriptionRepository;
import com.meln.app.subscription.model.Subscription;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import javax.naming.AuthenticationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
class SubscriptionScheduler {

  private final EventService eventService;
  private final EventProviderRegistry eventProviderRegistry;
  private final SubscriptionRepository subscriptionRepo;
  private final CalendarService calendarService;
  private final CalendarProviderRegistry calendarRegistry;

  @Scheduled(every = "15m")
  void syncAll() {
    var calendarPropsByUserId = calendarService.getCalendarIntegrationPropsByUserId();
    var activeSubscriptions = subscriptionRepo.find(Subscription.COL_ACTIVE, true).list();
    for (var subscription : activeSubscriptions) {
      var criteria = subscription.getCriteria();
      var eventProvider = eventProviderRegistry.get(criteria);

      try {
        var events = eventProvider.fetch(criteria);
        if (events.isEmpty()) {
          continue;
        }

        var userProps = calendarPropsByUserId.get(subscription.getUserId());
        createOrUpdateEvents(userProps, events);
        eventService.saveOrUpdate(events);
      } catch (Exception e) {
        log.error("Sync failed for subscription: {}", subscription.id, e);
      }
    }
  }

  private void createOrUpdateEvents(CalendarConnectionProperties userProperties,
      List<EventPayload> events) throws AuthenticationException {
    var calendarClient = calendarRegistry.connect(userProperties);
    for (var event : events) {
      if (event.getCalendarEventSourceId() != null) {
        calendarClient.updateEvent(event);
      } else {
        String newEventId = calendarClient.createEvent(event);
        event.setCalendarEventSourceId(newEventId);
      }
    }
  }

}
