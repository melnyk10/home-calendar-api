package com.meln.app.subscription;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionService {

  private final SubscriptionRepo subscriptionRepo;

  public List<Subscription> getAllUserSubscriptions(String userId) {
    return subscriptionRepo.find("userId", new ObjectId(userId)).list();
  }
}
