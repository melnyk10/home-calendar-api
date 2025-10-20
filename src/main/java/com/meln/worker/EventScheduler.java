package com.meln.worker;

import com.meln.app.event.EventRegistry;
import com.meln.app.event.EventService;
import com.meln.app.event.model.ProviderType;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@ApplicationScoped
public class EventScheduler {

  private final EventRegistry eventRegistry;
  private final EventService eventService;

//  @Scheduled(every = "15m")
  public void hltv() {
    var hltvProvider = eventRegistry.get(ProviderType.HLTV);
    var events = hltvProvider.fetchAll();
    if (!events.isEmpty()) {
      eventService.saveOrUpdate(events);
    }
  }

}
