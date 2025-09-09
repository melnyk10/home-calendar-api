package com.meln.app.event;

import com.meln.app.common.event.Criteria;
import com.meln.app.event.model.EventPayload;
import java.util.List;

public interface EventProvider<C extends Criteria> {

  Class<C> criteriaType();

  List<EventPayload> fetch(C criteria);
}
