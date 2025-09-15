package com.meln.app.event;

import com.meln.app.common.error.CustomException.CustomBadRequestException;
import com.meln.app.common.error.ErrorMessage;
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
      throw new CustomBadRequestException(ErrorMessage.Event.Code.INVALID_EVENT_PROVIDER_PROPERTIES,
          ErrorMessage.Event.Message.EVENT_PROVIDER_PROPERTIES_NOT_PROVIDED);
    }

    var eventProvider = (EventProvider<C>) eventProviderByCriteria.get(criteria.getClass());
    if (eventProvider == null) {
      throw new CustomBadRequestException(ErrorMessage.Event.Code.INVALID_EVENT_PROVIDER,
          ErrorMessage.Event.Message.NO_PROVIDER_BY_PROPS(criteria.getClass().getName()));
    }
    return eventProvider;
  }
}
