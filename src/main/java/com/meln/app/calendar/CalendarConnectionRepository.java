package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarConnection;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CalendarConnectionRepository implements PanacheRepository<CalendarConnection> {

  public CalendarConnection findByUserEmail(String email) {
    return null;
  }

  public void save(CalendarConnection googleToken) {

  }

  public void delete(CalendarConnection connection) {

  }
}
