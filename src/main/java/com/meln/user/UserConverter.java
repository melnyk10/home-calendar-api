package com.meln.user;

import com.meln.common.user.UserDto;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConverter {

  public UserDto toUserDto(User entity) {
    return Optional.ofNullable(entity)
        .map(e -> UserDto.builder()
            .id(e.id.toString())
            .firstName(e.getFirstName())
            .lastName(e.getLastName())
            .email(e.getEmail())
            .build())
        .orElse(null);
  }

  public static UserMe toUserMe(User entity) {
    return Optional.ofNullable(entity)
        .map(user -> new UserMe(entity.getFirstName(), entity.getLastName(), entity.getEmail()))
        .orElse(null);
  }

}
