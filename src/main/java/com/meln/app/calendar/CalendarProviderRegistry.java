package com.meln.app.calendar;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import javax.naming.AuthenticationException;

@ApplicationScoped
public class CalendarProviderRegistry {

  private final Map<Class<? extends CalendarProperties>, CalendarClient<?>> calendarClientByProperties = new HashMap<>();

  @Inject
  public CalendarProviderRegistry(Instance<CalendarClient<?>> calendarClients) {
    calendarClients.forEach(p -> calendarClientByProperties.put(p.propertiesType(), p));
  }

  @SuppressWarnings("unchecked")
  public <C extends CalendarProperties> CalendarClient<C> resolveAndAuthenticate(C props)
      throws AuthenticationException {
    if (props == null) {
      throw new IllegalArgumentException("Calendar properties not provided");
    }

    var calendarClient = (CalendarClient<C>) calendarClientByProperties.get(props.getClass());
    if (calendarClient == null) {
      throw new IllegalArgumentException("No provider for " + props.getClass().getName());
    }

    calendarClient.connect(props);
    return calendarClient;
  }

}
