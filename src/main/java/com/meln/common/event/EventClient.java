package com.meln.common.event;

import java.util.Collection;

public interface EventClient {
    void saveOrUpdate(Collection<EventDto> events);
}
