package com.meln.app.calendar.provider.google;

import com.meln.app.subscription.model.Subscription;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

@ApplicationScoped
public class GoogleTokenRepository implements PanacheMongoRepository<GoogleToken> {

  public GoogleToken findByUserId(String userId) {
    return find(Subscription.COL_USER_ID, new ObjectId(userId)).firstResult();
  }
}
