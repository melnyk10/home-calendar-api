package com.meln.app.user;

import com.meln.app.user.model.UserEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class UserCalendarRepository implements PanacheRepository<UserEvent> {

  public List<UserEvent> findAllByEventIds(Collection<Long> eventIds) {
    if (eventIds == null || eventIds.isEmpty()) {
      return List.of();
    }
    return find("eventId IN ?1", eventIds).list();
  }

}
