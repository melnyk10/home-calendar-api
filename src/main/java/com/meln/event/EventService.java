package com.meln.event;

import com.meln.common.event.EventDto;
import com.meln.common.event.EventClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EventService implements EventClient {
    private final EventRepo eventRepo;

    @Override
    public void saveOrUpdate(Collection<EventDto> events) {
        List<Event> eventEntities = events.stream().map(EventConverter::from).toList();
        eventRepo.bulkUpsert(eventEntities);
    }
}
