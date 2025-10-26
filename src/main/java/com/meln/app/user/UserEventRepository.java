package com.meln.app.user;

import com.meln.app.user.model.UserEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;

@ApplicationScoped
public class UserEventRepository implements PanacheRepository<UserEvent> {

  public void saveAll(Collection<UserEvent> events) {
    persist(events);
  }

}
