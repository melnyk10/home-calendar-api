package com.meln.event;

import com.meln.subscription.Subscription;

import java.util.List;

public interface EventProvider {
    Provider provider();

    List<Event> fetch(Subscription subscription);
}
