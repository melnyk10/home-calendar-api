package com.meln.app.event;

import com.meln.app.event.model.Event;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor
class EventRepository implements PanacheRepository<Event> {

  @SuppressWarnings("unchecked")
  public List<Event> findAllMissingUserEvents() {
    var sql = """
        select e.* from event e
                 join event_target et on e.id = et.event_id
                 join user_subscription us on us.target_id = et.target_id
        where not exists (select 1
                          from user_event ue
                          where ue.event_id = e.id)
        """;
    return getEntityManager()
        .createNativeQuery(sql, Event.class)
        .getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<Event> findAllChangedUserEvents() {
    var sql = """
        select e.*
        from event e
                 join event_target et on e.id = et.event_id
                 join user_event ue on ue.event_id = e.id
        where e.hash != ue.hash
        """;
    return getEntityManager()
        .createNativeQuery(sql, Event.class)
        .getResultList();
  }

  @Transactional
  public void upsertAll(List<Event> events) {
    var sourceIds = events.stream().map(Event::getSourceId).collect(Collectors.toSet());
    var eventBySourceId = find("sourceId in :sourceIds",
        Parameters.with("sourceIds", sourceIds)).list().stream()
        .collect(Collectors.toMap(Event::getSourceId, Function.identity()));

    List<Event> eventsToSave = new ArrayList<>();
    for (var event : events) {
      var existedEvent = eventBySourceId.get(event.getSourceId());
      if (existedEvent != null) {
        existedEvent.setName(event.getName());
        existedEvent.setDetails(event.getDetails());
        existedEvent.setPayload(event.getPayload());
        existedEvent.setTargets(event.getTargets());
        existedEvent.setAllDay(event.isAllDay());
        existedEvent.setStartAt(event.getStartAt());
        existedEvent.setEndAt(event.getEndAt());
        eventsToSave.add(existedEvent);
      } else {
        eventsToSave.add(event);
      }
    }

    persist(eventsToSave);
  }

}
