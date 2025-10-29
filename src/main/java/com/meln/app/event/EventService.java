package com.meln.app.event;

import com.meln.app.common.error.ErrorMessage;
import com.meln.app.common.error.ServerException;
import com.meln.app.event.model.Event;
import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.EventPayload.TargetPayload;
import com.meln.app.event.model.Target;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class EventService {

  private final EventRepository eventRepository;
  private final ProviderRepository providerRepository;
  private final TargetRepository targetRepository;

  public List<Event> listChangedUserEvents() {
    return eventRepository.findAllChangedUserEvents();
  }

  public List<Event> listMissingUserEvents() {
    return eventRepository.findAllMissingUserEvents();
  }

  @Transactional
  public void saveOrUpdate(Collection<EventPayload> events) {
    var eventEntities = events.stream().map(this::from).toList();
    eventRepository.upsertAll(eventEntities);
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

    if (eventPayload.payload() != null && !eventPayload.payload().isEmpty()) {
      event.setPayload(eventPayload.payload());
    }

    return event;
  }

  //todo: decouple save and get?
  private Set<Target> getTargets(Integer providerId, EventPayload eventPayload) {
    var targetSourceIds = eventPayload.targets().stream()
        .map(TargetPayload::sourceId)
        .map(String::valueOf)
        .toList();

    var eventTargets = targetRepository.listAllBySourceIdIn(providerId, targetSourceIds);
    var eventTargetBySourceId = eventTargets.stream()
        .collect(Collectors.toMap(it -> it.getSourceId(), Function.identity()));

    Set<Target> allTargets = new HashSet<>();
    Set<Target> newTargets = new HashSet<>();
    for (var target : eventPayload.targets()) {
      var eventTarget = eventTargetBySourceId.get(target.sourceId());
      if (eventTarget == null) {
        eventTarget = Target.builder()
            .sourceId(target.sourceId())
            .name(target.name())
            .type(target.type())
            .providerId(providerId)
            .data(target.data())
            .build();
        newTargets.add(eventTarget);
      }
      allTargets.add(eventTarget);
    }

    targetRepository.saveAll(newTargets);
    return allTargets;
  }
}
