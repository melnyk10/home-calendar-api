package com.meln.app.subscription;

import com.meln.app.subscription.model.Subscription;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionService {

  private final SubscriptionRepo subscriptionRepo;

  public List<Subscription> getAllUserSubscriptions(String userId) {
    return subscriptionRepo.find(Subscription.COL_USER_ID, new ObjectId(userId)).list();
  }
}
