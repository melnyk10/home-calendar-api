package com.meln.app.subscription.model;

import com.meln.app.common.event.EventProviderCriteria;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Getter
@Setter
@MongoEntity(collection = "subscriptions")
public class Subscription extends PanacheMongoEntity {
  public static final String COL_USER_ID = "userId";
  public static final String COL_ACTIVE = "active";

  @BsonProperty(COL_USER_ID)
  private ObjectId userId;

  @BsonProperty(COL_ACTIVE)
  private boolean active = true;

  @BsonProperty(value = "criteria", useDiscriminator = true)
  public EventProviderCriteria criteria;
}
