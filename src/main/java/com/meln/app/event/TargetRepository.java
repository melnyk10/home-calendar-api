package com.meln.app.event;

import com.meln.app.event.model.Target;
import com.meln.app.event.model.ProviderType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TargetRepository implements PanacheRepository<Target> {

  public List<Target> listAllByProvider(ProviderType type) {
    String jpql = """
        select et from EventTarget et
            join et.provider p
        where p.type = :type
        """;
    return find(jpql, Parameters.with("type", type)).list();
  }

  List<Target> listAllBySourceIdIn(Integer providerId, Collection<String> sourceIds) {
    String jpql = """
        select et from EventTarget et
            join et.provider p
        where p.id = :providerId and et.sourceId in (:sourceIds)
        """;
    return find(jpql, Map.of("providerId", providerId, "sourceIds", sourceIds)).list();
  }

  void saveAll(Collection<Target> targets) {
    persist(targets);
  }
}
