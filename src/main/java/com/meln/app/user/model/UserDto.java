package com.meln.app.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private String id;
  private String firstName;
  private String lastName;
  private String email;

  public static UserDto from(User entity) {
    if (entity == null) {
      return null;
    }
    return UserDto.builder()
        .id(entity.id.toString())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .email(entity.getEmail())
        .build();
  }
}
