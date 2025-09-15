package com.meln.app.calendar;

import com.meln.app.common.error.CustomException.CustomBadRequestException;
import com.meln.app.common.error.ErrorMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import javax.naming.AuthenticationException;

@ApplicationScoped
public class CalendarProviderRegistry {

  private final Map<Class<? extends CalendarConnectionProperties>, CalendarClient<?>> byProps = new HashMap<>();

  @Inject
  public CalendarProviderRegistry(Instance<CalendarClient<?>> calendarClients) {
    calendarClients.forEach(c -> byProps.put(c.propertiesType(), c));
  }

  @SuppressWarnings("unchecked")
  public <P extends CalendarConnectionProperties> CalendarClient.CalendarConnection connect(P props)
      throws AuthenticationException {
    if (props == null) {
      throw new CustomBadRequestException(
          ErrorMessage.Calendar.Code.INVALID_CALENDAR_PROVIDER_PROPERTIES,
          ErrorMessage.Calendar.Message.CALENDAR_PROVIDER_PROPERTIES_NOT_PROVIDED);
    }

    CalendarClient<P> client = (CalendarClient<P>) byProps.get(props.getClass());
    if (client == null) {
      throw new CustomBadRequestException(ErrorMessage.Calendar.Code.INVALID_CALENDAR_PROVIDER,
          ErrorMessage.Calendar.Message.NO_PROVIDER_BY_PROPS(props.getClass().getName()));
    }
    return client.connect(props);
  }
}
