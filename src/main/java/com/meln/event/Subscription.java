package com.meln.event;

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
    @BsonProperty("user_id")
    private ObjectId userId;

    @BsonProperty("provider")
    private Provider provider;

    @BsonProperty("active")
    private boolean active = true;

    @BsonProperty(value = "criteria", useDiscriminator = true)
    public Criteria criteria;
}
