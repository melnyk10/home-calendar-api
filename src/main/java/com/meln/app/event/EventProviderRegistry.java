package com.meln.app.event;

import com.meln.app.common.event.EventProviderCriteria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class EventProviderRegistry {

  private final Map<Class<? extends EventProviderCriteria>, EventProvider<? extends EventProviderCriteria>> eventProviderByCriteria = new HashMap<>();

  @Inject
  public EventProviderRegistry(Instance<EventProvider<? extends EventProviderCriteria>> providers) {
    providers.forEach(p -> eventProviderByCriteria.put(p.criteriaType(), p));
  }

  @SuppressWarnings("unchecked")
  public <C extends EventProviderCriteria> EventProvider<C> get(C criteria) {
    if (criteria == null) {
      throw new IllegalArgumentException("Calendar properties not provided");
    }

    var eventProvider = (EventProvider<C>) eventProviderByCriteria.get(criteria.getClass());
    if (eventProvider == null) {
      throw new IllegalArgumentException("No provider for " + criteria.getClass().getName());
    }
    return eventProvider;
  }
}
