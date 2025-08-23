package com.meln.common.event;

import java.util.List;

public interface EventProvider<C extends Criteria> {
    Class<C> criteriaType();

    List<EventDto> fetch(C criteria);
}
