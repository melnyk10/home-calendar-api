package com.meln.user;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "users")
public class User extends PanacheMongoEntity {
    private String firstName;
    private String lastName;
    private String email;
}
