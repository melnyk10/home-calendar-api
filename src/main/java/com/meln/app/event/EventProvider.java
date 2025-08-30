package com.meln.app.event;

import com.meln.common.event.Criteria;
import java.util.List;

public interface EventProvider<C extends Criteria> {

  Class<C> criteriaType();

  List<EventDto> fetch(C criteria);
}
