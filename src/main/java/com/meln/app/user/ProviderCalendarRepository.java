package com.meln.app.user;

import com.meln.app.user.model.ProviderCalendar;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class ProviderCalendarRepository implements PanacheRepository<ProviderCalendar> {

  @SuppressWarnings("unchecked")
  public List<ProviderCalendar> findAllByTargetIdsIn(Collection<Long> targetIds) {
    String sql = """
        select pc.*
        from provider_calendar pc
                 join target t on t.provider_id = pc.provider_id
        where t.id in (:targetIds)
        """;
    return getEntityManager()
        .createNativeQuery(sql, ProviderCalendar.class)
        .setParameter("targetIds", targetIds)
        .getResultList();
  }
}
