package com.meln.worker;

import com.meln.app.event.EventProviderRegistry;
import com.meln.app.event.EventService;
import com.meln.app.subscription.SubscriptionRepo;
import com.meln.app.subscription.model.Subscription;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionScheduler {

  private final EventService eventService;
  private final EventProviderRegistry eventProviderRegistry;
  private final SubscriptionRepo subscriptionRepo;

  @Scheduled(every = "15m")
  void syncAll() {
    var activeSubscriptions = subscriptionRepo.find(Subscription.COL_ACTIVE, true).list();
    for (var subscription : activeSubscriptions) {
      try {
        var criteria = subscription.getCriteria();
        var eventProvider = eventProviderRegistry.get(criteria);
        var events = eventProvider.fetch(criteria);
        if (!events.isEmpty()) {
          eventService.saveOrUpdate(events);
        }
      } catch (Exception e) {
        log.error("Sync failed for subscription: {}", subscription.id, e);
      }
    }
  }
}
