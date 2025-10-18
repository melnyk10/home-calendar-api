package com.meln.app.subscription;

import com.meln.app.subscription.model.Subscription;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SubscriptionRepository implements PanacheRepository<Subscription> {

  public List<Subscription> listUserSubscriptions(String userId) {
    return new ArrayList<>();
  }
}
