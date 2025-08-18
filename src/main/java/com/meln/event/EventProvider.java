package com.meln.event;

import java.util.List;

public interface EventProvider {
    Provider provider();

    List<Event> fetch(Subscription subscription);
}
