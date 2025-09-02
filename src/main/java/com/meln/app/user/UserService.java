package com.meln.app.user;

import com.meln.app.common.CacheNames;
import com.meln.app.user.model.UserInfo;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class UserService {

  private final UserRepository userRepo;

  public UserInfo getByEmail(String email) {
    var user = userRepo.findByEmail(email);
    return UserInfo.from(user);
  }

  //todo: don't forget to add @CacheInvalidate for future deleteUser method
  @CacheResult(cacheName = CacheNames.USER_EXISTS_CACHE_NAME)
  public boolean existsByEmail(String email) {
    return userRepo.existsByEmail(email);
  }

}
