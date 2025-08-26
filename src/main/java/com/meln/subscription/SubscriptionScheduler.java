package com.meln.subscription;

import com.meln.common.event.EventClient;
import com.meln.common.event.EventProviderRegistry;
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
    private final EventClient eventClient;
    private final EventProviderRegistry registry;
    private final SubscriptionRepo subscriptionRepo;

    @Scheduled(every = "15m")
    void syncAll() {
        List<Subscription> activeSubscriptions = subscriptionRepo.find("active", true).list();
        for (Subscription sub : activeSubscriptions) {
            try {
                var criteria = sub.getCriteria();
                var eventProvider = registry.get(criteria);
                var events = eventProvider.fetch(criteria);
                if (!events.isEmpty()) {
                    eventClient.saveOrUpdate(events);
                }
            } catch (Exception e) {
                log.error("Sync failed for sub: {}", sub.id, e);
            }
        }
    }
}
