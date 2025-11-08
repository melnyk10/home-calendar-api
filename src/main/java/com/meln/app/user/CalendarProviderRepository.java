package com.meln.app.user;

import com.meln.app.user.model.CalendarProvider;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Collection;

@ApplicationScoped
public class CalendarProviderRepository implements PanacheRepository<CalendarProvider> {

  @Transactional
  void save(Collection<CalendarProvider> providers) {
    persist(providers);
  }
}
