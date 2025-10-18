package com.meln.app.event;

import com.meln.app.event.model.Subject;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class SubjectRepository implements PanacheRepository<Subject> {

  public Optional<Subject> findByProviderAndTypeAndExternalId(Long id, String type, String id1) {
    return Optional.empty();
  }

  public Subject save(Subject newSubject) {
    return null;
  }
}
