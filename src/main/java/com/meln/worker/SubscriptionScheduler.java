package com.meln.worker;

import com.meln.app.calendar.CalendarProviderRegistry;
import com.meln.app.calendar.CalendarService;
import com.meln.app.event.EventProviderRegistry;
import com.meln.app.event.EventService;
import com.meln.app.subscription.SubscriptionRepository;
import com.meln.app.subscription.model.Subscription;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
        try {
          var conn = calendarRegistry.connect(userProps);
          for (var event : events) {
            if (event.getCalendarEventSourceId() != null) {
              conn.updateEvent(event);
            } else {
              String newEventId = conn.createEvent(event);
              event.setCalendarEventSourceId(newEventId);
            }
          }
        } catch (Exception e) {
          log.warn("Calendar sync failed (userId={}): {}", subscription.getUserId(),
              e.getMessage());
          continue;
        }

        if (!events.isEmpty()) {
          eventService.saveOrUpdate(events);
        }
      } catch (Exception e) {
        log.error("Sync failed for subscription: {}", subscription.id, e);
      }

    }
  }

}
