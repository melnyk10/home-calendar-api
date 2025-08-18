package com.meln.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EventService {
    private final EventRepo eventRepo;

    public void saveOrUpdate(Collection<Event> events) {
        eventRepo.bulkUpsert(events);
    }
}
