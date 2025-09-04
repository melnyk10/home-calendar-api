package com.meln.worker;

import com.meln.app.calendar.CalendarClient;
import com.meln.app.calendar.CalendarProperties;
import com.meln.app.calendar.CalendarProviderRegistry;
import com.meln.app.calendar.CalendarService;
import com.meln.app.event.EventProviderRegistry;
import com.meln.app.event.EventService;
import com.meln.app.subscription.SubscriptionRepository;
import com.meln.app.subscription.model.Subscription;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.naming.AuthenticationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionScheduler {

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
      try {
        var criteria = subscription.getCriteria();
        var eventProvider = eventProviderRegistry.get(criteria);
        var events = eventProvider.fetch(criteria);

        CalendarClient<CalendarProperties> calendarClient;
        try {
          var userCalendarProps = calendarPropsByUserId.get(subscription.getUserId());
          calendarClient = calendarRegistry.resolveAndAuthenticate(userCalendarProps);
        } catch (AuthenticationException e) {
          log.error("Can't authenticate with calendar provider for userId: {}",
              subscription.getUserId(), e);
          continue;
        }

        for (var event : events) {
          try {
            if (event.getCalendarEventSourceId() != null) {
              calendarClient.updateEvent(event.getCalendarEventSourceId(), event);
            } else {
              String newEventId = calendarClient.createEvent(event);
              event.setCalendarEventSourceId(newEventId);
            }
          } catch (Exception e) {
            events.remove(event);
            log.error("Error while creating or updating event in calendar. Skipping...", e);
          }
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
