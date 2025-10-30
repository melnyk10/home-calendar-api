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
        select t from Target t
            join Provider p on p.id = t.providerId
        where p.type = :type
        """;
    return find(jpql, Parameters.with("type", type)).list();
  }

  List<Target> listAllBySourceIdIn(Integer providerId, Collection<String> sourceIds) {
    String jpql = """
        select t from Target t
            join Provider p on p.id = t.providerId
        where p.id = :providerId and t.sourceId in (:sourceIds)
        """;
    return find(jpql, Map.of("providerId", providerId, "sourceIds", sourceIds)).list();
  }

  void saveAll(Collection<Target> targets) {
    persist(targets);
  }
}
