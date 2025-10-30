package com.meln.app.event;

import com.meln.app.event.model.Provider;
import com.meln.app.event.model.ProviderType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ProviderRepository implements PanacheRepository<Provider> {

  public Optional<Provider> findByType(ProviderType type) {
    return find("type", type).singleResultOptional();
  }
}
