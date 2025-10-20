package com.meln.app.event;

import com.meln.app.event.model.Subject;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class SubjectRepository implements PanacheRepository<Subject> {

  public Optional<Subject> findByProviderAndTypeAndSourceId(Integer providerId, String type,
      String sourceId) {
    return Optional.empty();
  }

  public Subject save(Subject subject) {
    return null;
  }
}
