package com.meln.app.event;

import com.meln.app.common.event.EventProviderCriteria;
import com.meln.app.event.model.EventPayload;
import java.util.List;

public interface EventProvider<C extends EventProviderCriteria> {

  Class<C> criteriaType();

  List<EventPayload> fetch(C criteria);
}
