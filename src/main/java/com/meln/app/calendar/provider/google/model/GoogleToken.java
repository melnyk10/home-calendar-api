package com.meln.app.calendar.provider.google.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "googleTokens")
public class GoogleToken extends PanacheMongoEntity {

  public static final String COL_USER_EMAIL = "userEmail";
  public static final String COL_GOOGLE_SUB = "googleSub";
  public static final String COL_EMAIL = "email";
  public static final String COL_ACCESS_TOKEN = "accessToken";
  public static final String COL_REFRESH_TOKEN = "refreshToken";
  public static final String COL_EXPIRES_AT = "expiresAt";
  public static final String COL_SCOPES = "scopes";

  @BsonProperty(COL_USER_EMAIL)
  private String userEmail;

  @BsonProperty(COL_GOOGLE_SUB)
  private String googleSub;

  @BsonProperty(COL_EMAIL)
  private String email;

  @BsonProperty(COL_ACCESS_TOKEN)
  private String accessToken;

  @BsonProperty(COL_REFRESH_TOKEN)
  private String refreshToken;

  @BsonProperty(COL_EXPIRES_AT)
  private Instant expiresAt;

  @BsonProperty(COL_SCOPES)
  private Set<String> scopes;
}
