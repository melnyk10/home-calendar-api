package com.meln.app.event;

import com.meln.app.common.error.ErrorMessage;
import com.meln.app.common.error.ServerException;
import com.meln.app.event.model.ProviderType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class EventRegistry {

  private final Map<ProviderType, Provider> providerByType = new HashMap<>();

  @Inject
  public EventRegistry(Instance<Provider> providers) {
    providers.forEach(p -> providerByType.put(p.providerType(), p));
  }

  public Provider get(ProviderType type) {
    if (type == null) {
      throw new ServerException(ErrorMessage.Event.Code.INVALID_EVENT_PROVIDER_PROPERTIES,
          ErrorMessage.Event.Message.EVENT_PROVIDER_PROPERTIES_NOT_PROVIDED);
    }

    var eventProvider = providerByType.get(type);
    if (eventProvider == null) {
      throw new ServerException(ErrorMessage.Event.Code.INVALID_EVENT_PROVIDER,
          ErrorMessage.Event.Message.NO_PROVIDER_BY_PROPS(type.name()));
    }
    return eventProvider;
  }

}
