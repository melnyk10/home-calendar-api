package com.meln.app.event;

import com.meln.app.event.model.Provider;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ProviderRepository implements PanacheRepository<Provider> {

  public Optional<Provider> findByNameIgnoreCase(String provider) {
    return Optional.empty();
  }
}
