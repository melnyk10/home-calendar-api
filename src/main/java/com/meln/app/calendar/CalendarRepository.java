package com.meln.app.calendar;

import com.meln.app.calendar.model.Calendar;
import com.meln.app.calendar.model.CalendarProvider;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class CalendarRepository implements PanacheRepository<Calendar> {

  public Optional<Calendar> findByEmailAndProvider(String email, CalendarProvider provider) {
    return find("accountEmail = :email and provider = :provider",
        Parameters
            .with("email", email)
            .and("provider", provider))
        .firstResultOptional();
  }
}
