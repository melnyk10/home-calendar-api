package com.meln.common.event;

import java.util.Collection;

public interface EventSink {
    void saveOrUpdate(Collection<EventDto> events);
}
