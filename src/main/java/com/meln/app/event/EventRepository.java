package com.meln.app.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meln.app.event.model.Event;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@ApplicationScoped
@AllArgsConstructor
class EventRepository implements PanacheRepository<Event> {

  private final ObjectMapper objectMapper;
  @PersistenceContext
  private final EntityManager entityManager;

  @SuppressWarnings("unchecked")
  public List<Event> findAllByNotFoundEvents() {
    var sql = """
        select e.* from event e
                 join event_target et on e.id = et.event_id
                 join user_subscription us on us.target_id = et.target_id
        where not exists (select 1
                          from user_event uce
                          where uce.event_id = e.id)
        """;
    return getEntityManager()
        .createNativeQuery(sql, Event.class)
        .getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<Event> findAllByUserSubscriptionsEvents() {
    var sql = """
        select e.* from event e
                 join event_target et on e.id = et.event_id
                 join user_subscription us on us.target_id = et.target_id
        where e.hash != us.hash
        and exists (select 1
                      from user_event uce
                      where uce.event_id = e.id)
        """;
    return getEntityManager()
        .createNativeQuery(sql, Event.class)
        .getResultList();
  }

  @SneakyThrows
  @Transactional
  public void upsertAll(List<Event> events) {
    //todo: not very save sql query approach
    var sql = """
        INSERT INTO event
          (source_id, provider_id, type, name, details, is_all_day, start_at, end_at, created_at, updated_at, payload)
        VALUES
          %s
        ON CONFLICT (source_id) DO UPDATE SET
          provider_id = EXCLUDED.provider_id,
          type        = EXCLUDED.type,
          name        = EXCLUDED.name,
          details     = EXCLUDED.details,
          is_all_day  = EXCLUDED.is_all_day,
          start_at    = EXCLUDED.start_at,
          end_at      = EXCLUDED.end_at,
          updated_at  = now(),
          payload     = EXCLUDED.payload;
        """;

    var values = events.stream()
        .map(e -> "(?, ?, ?, ?, ?, ?, ?, ?, now(), now(), CAST(? AS jsonb))")
        .collect(Collectors.joining(","));

    var query = entityManager.createNativeQuery(String.format(sql, values));

    int idx = 1;
    for (Event e : events) {
      query.setParameter(idx++, e.getSourceId());
      query.setParameter(idx++, e.getProvider().getId());
      query.setParameter(idx++, e.getType().name());
      query.setParameter(idx++, e.getName());
      query.setParameter(idx++, e.getDetails());
      query.setParameter(idx++, e.isAllDay());
      query.setParameter(idx++, e.getStartAt());
      query.setParameter(idx++, e.getEndAt());

      if (e.getPayload() != null) {
        query.setParameter(idx++, objectMapper.writeValueAsString(e.getPayload()));
      } else {
        query.setParameter(idx++, "{}");
      }
    }

    query.executeUpdate();
    //todo: create event_target relations!
  }

}
