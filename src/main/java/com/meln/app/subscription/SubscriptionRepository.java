package com.meln.app.subscription;

import com.meln.app.subscription.model.Subscription;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.bson.types.ObjectId;

@ApplicationScoped
public class SubscriptionRepository implements PanacheMongoRepository<Subscription> {
  public List<Subscription> listUserSubscriptions(String userId) {
    return find(Subscription.COL_USER_ID, new ObjectId(userId)).list();
  }
}
