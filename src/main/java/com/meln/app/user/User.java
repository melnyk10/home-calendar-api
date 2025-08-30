package com.meln.app.user;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MongoEntity(collection = "users")
public class User extends PanacheMongoEntity {

  private String firstName;
  private String lastName;
  private String email;
}
