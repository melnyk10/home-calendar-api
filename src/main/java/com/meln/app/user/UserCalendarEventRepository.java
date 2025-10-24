package com.meln.app.user;

import com.meln.app.user.model.UserCalendarEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class UserCalendarEventRepository implements PanacheRepository<UserCalendarEvent> {

  public void saveAll(Collection<UserCalendarEvent> events) {
    persist(events);
  }

}
