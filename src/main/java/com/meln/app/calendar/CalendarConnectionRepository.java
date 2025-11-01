package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarConnection;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CalendarConnectionRepository implements PanacheRepository<CalendarConnection> {

  public CalendarConnection findByUserEmail(String email) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public CalendarConnection findAllCalendarConnections(String email, Integer providerId) {
    String sql = """
        select cc.*
        from calendar_connection cc
                 join provider_calendar pc on pc.calendar_connection_id = cc.id
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
