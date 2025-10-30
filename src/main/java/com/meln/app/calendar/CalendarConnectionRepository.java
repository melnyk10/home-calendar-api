package com.meln.app.calendar;

import com.meln.app.calendar.model.CalendarConnection;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CalendarConnectionRepository implements PanacheRepository<CalendarConnection> {

  public CalendarConnection findByUserEmail(String email) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void save(CalendarConnection googleToken) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void delete(CalendarConnection connection) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public CalendarConnection findAllCalendarConnections(Long userId, Integer providerId) {
    String sql = """
        select cc.*
        from calendar_connection cc
                 join provider_calendar pc on pc.calendar_connection_id = cc.id
        where cc.user_id = :userId
          and pc.provider_id = :providerId
        """;

    return (CalendarConnection) getEntityManager()
        .createNativeQuery(sql, CalendarConnection.class)
        .setParameter("userId", userId)
        .setParameter("providerId", providerId)
        .getSingleResult();
  }
}
