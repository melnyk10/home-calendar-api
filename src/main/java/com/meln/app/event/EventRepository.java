package com.meln.app.event;

import com.meln.app.event.model.Event;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
class EventRepository implements PanacheRepository<Event> {

  public List<Event> findAllByNotFoundEvents() {
    String sql = """
        select e
        from event e
                 join user_subscription us on us.provider_id = e.provider_id and us.subject_id = e.subject_id
        where not exists (select 1
                          from user_calendar_event uce
                          where uce.source_event_id = e.source_id)
        """;
    return find(sql).list();
  }

  public List<Event> findAllByUserSubscriptionsEvents() {
    String sql = """
        select e
        from event e
                 join user_subscription us on us.provider_id = e.provider_id and us.subject_id = e.subject_id
        where exists (select 1
                          from user_calendar_event uce
                          where uce.source_event_id = e.source_id)
        """;
    return find(sql).list();
  }

  public void bulkUpsert(List<Event> eventEntities) {
    //todo: finish me!
  }

}
