package com.meln.app.calendar;

import com.meln.app.calendar.model.Calendar;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class CalendarRepository implements PanacheRepository<Calendar> {

  List<Calendar> findByEmail(String email) {
    return find("email", email).list();
  }

  void deleteCalendars(String email, Collection<Integer> calendarIds) {
    String jpql = """
        delete
        from calendar
        where email = :email
          and id in (:calendarIds)
        """;
    delete(jpql, Parameters.with("email", email)
        .and("calendarIds", calendarIds));
  }
}
