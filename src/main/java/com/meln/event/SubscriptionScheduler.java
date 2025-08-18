package com.meln.event;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionScheduler {
    private final ProviderRegistry registry;
    private final EventService eventService;
    private final SubscriptionRepo subscriptionRepo;

    @Scheduled(every = "15m")
    void syncAll() {
        List<Subscription> activeSubscriptions = subscriptionRepo.find("active", true).list();
        for (Subscription sub : activeSubscriptions) {
            try {
                EventProvider eventProvider = registry.get(sub.getProvider());
                List<Event> events = eventProvider.fetch(sub);
                eventService.saveOrUpdate(events);
            } catch (Exception e) {
                log.error("Sync failed for sub {} ({}})", sub.id, sub.getProvider(), e);
            }
        }
    }
}
