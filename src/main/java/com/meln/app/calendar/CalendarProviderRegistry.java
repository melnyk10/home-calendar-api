package com.meln.app.calendar;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import javax.naming.AuthenticationException;

@ApplicationScoped
public class CalendarProviderRegistry {

  private final Map<Class<? extends CalendarProperties>, CalendarClient<?>> byProps = new HashMap<>();

  @Inject
  public CalendarProviderRegistry(Instance<CalendarClient<?>> calendarClients) {
    calendarClients.forEach(c -> byProps.put(c.propertiesType(), c));
  }

  @SuppressWarnings("unchecked")
  public <P extends CalendarProperties> CalendarClient.CalendarConnection connect(P props)
      throws AuthenticationException {
    CalendarClient<P> client = (CalendarClient<P>) byProps.get(props.getClass());
    if (client == null) {
      throw new IllegalArgumentException("No provider for " + props.getClass().getName());
    }
    return client.connect(props);
  }
}
