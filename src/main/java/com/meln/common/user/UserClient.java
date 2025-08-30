package com.meln.common.user;

public interface UserClient {

  UserDto getByEmail(String email);

  boolean existsByEmail(String email);
}
