package com.meln.app.event;

import com.meln.app.common.event.EventProviderCriteria;
import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.ProviderType;
import java.util.List;

public interface EventProvider<C extends EventProviderCriteria> {
  ProviderType providerType();

  List<EventPayload> fetchAll();
}
