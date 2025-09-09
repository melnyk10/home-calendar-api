package com.meln.app.user.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Getter
@Setter
@MongoEntity(collection = "users")
public class User extends PanacheMongoEntity {

  public static final String COL_EMAIL = "email";
  public static final String COL_FIRST_NAME = "firstName";
  public static final String COL_LAST_NAME = "lastName";

  @BsonProperty(COL_FIRST_NAME)
  private String firstName;

  @BsonProperty(COL_LAST_NAME)
  private String lastName;

  @BsonProperty(COL_EMAIL)
  private String email;

  public ObjectId getId() {
    return this.id;
  }
}
