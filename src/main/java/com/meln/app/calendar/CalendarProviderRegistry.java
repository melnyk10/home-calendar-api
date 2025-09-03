package com.meln.app.calendar;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CalendarProviderRegistry {

  private final Map<Class<? extends CalendarIntegrationProperties>, CalendarIntegrationClient<?>> calendarClientByProperties = new HashMap<>();

  @Inject
  public CalendarProviderRegistry(Instance<CalendarIntegrationClient<?>> calendarClients) {
    calendarClients.forEach(p -> calendarClientByProperties.put(p.propertiesType(), p));
  }

  @SuppressWarnings("unchecked")
  public <C extends CalendarIntegrationProperties> CalendarIntegrationClient<C> get(C criteria) {
    var calendarClient = (CalendarIntegrationClient<C>) calendarClientByProperties.get(
        criteria.getClass());
    if (calendarClient == null) {
      throw new IllegalArgumentException("No provider for " + criteria.getClass().getName());
    }
    return calendarClient;
  }

}
