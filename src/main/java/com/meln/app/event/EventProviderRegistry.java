package com.meln.app.event;

import com.meln.app.common.event.Criteria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class EventProviderRegistry {

  private final Map<Class<? extends Criteria>, EventProvider<? extends Criteria>> eventProviderByCriteria = new HashMap<>();

  @Inject
  public EventProviderRegistry(Instance<EventProvider<? extends Criteria>> providers) {
    providers.forEach(p -> eventProviderByCriteria.put(p.criteriaType(), p));
  }

  @SuppressWarnings("unchecked")
  public <C extends Criteria> EventProvider<C> get(C criteria) {
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
