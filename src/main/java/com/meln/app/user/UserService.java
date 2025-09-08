package com.meln.app.user;

import com.meln.app.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class UserService {

  private final UserRepository userRepo;

  public User getByEmail(String email) {
    return userRepo.findByEmail(email);
  }

  boolean existsByEmail(String email) {
    return userRepo.existsByEmail(email);
  }

}
