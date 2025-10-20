package com.meln.app.event;

import com.meln.app.event.model.Event;
import com.meln.app.event.model.EventPayload;
import com.meln.app.event.model.Subject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class EventService {

  private final EventRepository eventRepository;
  private final ProviderRepository providerRepository;
  private final SubjectRepository subjectRepository;

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
    var provider = providerRepository.findByNameIgnoreCase(eventPayload.provider())
        .orElseThrow(
            () -> new IllegalArgumentException("Unknown provider: " + eventPayload.provider()));

    var subject = subjectRepository.findByProviderAndTypeAndSourceId(
            provider.getId(), eventPayload.subject().type(), eventPayload.subject().id())
        .orElseGet(() -> {
          var newSubject = new Subject();
          newSubject.setProvider(provider);
          newSubject.setType(eventPayload.subject().type());
          newSubject.setSourceId(eventPayload.subject().id());
          newSubject.setName(eventPayload.subject().name());
          newSubject.setData(eventPayload.subject().externalRefs() == null ? Map.of()
              : eventPayload.subject().externalRefs());
          return subjectRepository.save(newSubject);
        });

    var event = new Event();
    event.setProvider(provider);
    event.setSubject(subject);
    event.setType(eventPayload.eventType());
    event.setPayload(eventPayload.payload() == null ? Map.of() : eventPayload.payload());

    return event;
  }
}
