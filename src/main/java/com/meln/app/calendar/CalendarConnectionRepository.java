package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarConnection;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class CalendarConnectionRepository implements PanacheRepository<CalendarConnection> {

  public Optional<CalendarConnection> findByUserEmail(String email) {
    return find("email", email).firstResultOptional();
  }

  CalendarConnection findAllCalendarConnections(String email, Integer providerId) {
    String sql = """
        select cc.*
        from calendar_connection cc
                 join calendar c on c.email = cc.email
                 join provider_calendar pc on pc.calendar_id = cc.id
        where cc.email = :email
          and pc.provider_id = :providerId
        """;

    return (CalendarConnection) getEntityManager()
        .createNativeQuery(sql, CalendarConnection.class)
        .setParameter("email", email)
        .setParameter("providerId", providerId)
        .getSingleResultOrNull();
  }
}
