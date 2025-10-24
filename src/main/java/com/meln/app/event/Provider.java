package com.meln.app.event;

import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.ProviderType;
import java.util.List;

public interface Provider {
  ProviderType providerType();

  List<EventPayload> fetchAll();
}
