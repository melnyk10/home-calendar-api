<<<<<<<< HEAD:src/main/java/com/meln/app/user/model/User.java
package com.meln.app.user.model;
========
package com.meln.app.user;
>>>>>>>> 4626548 (move files):src/main/java/com/meln/app/user/User.java

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MongoEntity(collection = "users")
public class User extends PanacheMongoEntity {

  public static final String COL_EMAIL = "email";

  private String firstName;
  private String lastName;
  private String email;
}
