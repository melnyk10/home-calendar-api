package com.meln.app.user;

import com.meln.common.CacheNames;
import com.meln.common.user.UserClient;
import com.meln.common.user.UserDto;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserService implements UserClient {

  private final UserRepo userRepo;

  public UserMe me(String email) {
    User user = userRepo.findByEmail(email);
    return UserConverter.toUserMe(user);
  }

  public UserDto getByEmail(String email) {
    User user = userRepo.findByEmail(email);
    return UserConverter.toUserDto(user);
  }

  @Override
  @CacheResult(cacheName = CacheNames.USER_EXISTS_CACHE_NAME)
  //todo: don't forget to add @CacheInvalidate for future deleteUser method
  public boolean existsByEmail(String email) {
    return userRepo.existsByEmail(email);
  }

}
