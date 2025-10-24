package com.meln.app.event;

import com.meln.app.common.error.ErrorMessage;
import com.meln.app.common.error.ServerException;
import com.meln.app.event.model.Event;
import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.EventPayload.TargetPayload;
import com.meln.app.event.model.EventTarget;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class EventService {

  private final EventRepository eventRepository;
  private final ProviderRepository providerRepository;
  private final EventTargetRepository eventTargetRepository;

  public List<Event> listAllChangedEvents() {
    return eventRepository.findAllByUserSubscriptionsEvents();
  }

  public List<Event> findAllByNotFoundEvents() {
    return eventRepository.findAllByNotFoundEvents();
  }

  public void saveOrUpdate(Collection<EventPayload> events) {
    var eventEntities = events.stream().map(this::from).toList();
    eventRepository.bulkUpsert(eventEntities);
  }

  public Event from(EventPayload eventPayload) {
    //todo: Cache me!
    var provider = providerRepository.findByType(eventPayload.provider())
        .orElseThrow(() -> new ServerException(ErrorMessage.Provider.Code.UNKNOWN_PROVIDER,
            ErrorMessage.Provider.Message.UNKNOWN_PROVIDER(eventPayload.provider().name())));

    var event = new Event();
    event.setSourceId(eventPayload.sourceId());
    event.setProvider(provider);
    event.setTargets(getTargets(provider.getId(), eventPayload));
    event.setType(eventPayload.type());
    event.setName(eventPayload.name());
    event.setDetails(eventPayload.details());
    event.setPayload(null);
    event.setAllDay(eventPayload.isAllDay());
    event.setStartAt(eventPayload.startAt());
    event.setEndAt(eventPayload.endAt());

    return event;
  }

  //todo: decouple save and get?
  private List<EventTarget> getTargets(Integer providerId, EventPayload eventPayload) {
    var targetSourceIds = eventPayload.targets().stream()
        .map(TargetPayload::id)
        .map(String::valueOf)
        .toList();

    var eventTargets = eventTargetRepository.listAllBySourceIdIn(providerId, targetSourceIds);
    var eventTargetBySourceId = eventTargets.stream()
        .collect(Collectors.toMap(EventTarget::getSourceId, Function.identity()));

    List<EventTarget> allTargets = new ArrayList<>();
    List<EventTarget> newEventTargets = new ArrayList<>();
    for (var target : eventPayload.targets()) {
      var eventTarget = eventTargetBySourceId.get(target.id());
      if (eventTarget == null) {
        eventTarget = EventTarget.builder()
            .sourceId(target.id())
            .name(target.name())
            .type(target.type())
            .providerId(providerId)
            .data(target.data())
            .build();
        newEventTargets.add(eventTarget);
      }
      allTargets.add(eventTarget);
    }

    eventTargetRepository.saveAll(newEventTargets);
    return allTargets;
  }
}
