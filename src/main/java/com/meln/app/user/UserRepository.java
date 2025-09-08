package com.meln.app.user;

import com.meln.app.user.model.User;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class UserRepository implements PanacheMongoRepository<User> {

  public User findByEmail(String email) {
    return find(User.COL_EMAIL, email).firstResult();
  }

  public boolean existsByEmail(String email) {
    return find(User.COL_EMAIL, email).firstResultOptional().isPresent();
  }
}
