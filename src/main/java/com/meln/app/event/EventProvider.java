package com.meln.app.event;

import com.meln.app.common.event.Criteria;
import com.meln.app.event.model.EventDto;
import java.util.List;

public interface EventProvider<C extends Criteria> {

  Class<C> criteriaType();

  List<EventDto> fetch(C criteria);
}
