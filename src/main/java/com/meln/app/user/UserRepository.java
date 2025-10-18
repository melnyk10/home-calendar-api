package com.meln.app.user;

import com.meln.app.user.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

  public User findByEmail(String email) {
    return find(User.COL_EMAIL, email).firstResult();
  }

  public boolean existsByEmail(String email) {
    return find(User.COL_EMAIL, email).firstResultOptional().isPresent();
  }
}
