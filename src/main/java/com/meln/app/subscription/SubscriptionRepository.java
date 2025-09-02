package com.meln.app.subscription;

import com.meln.app.subscription.model.Subscription;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubscriptionRepository implements PanacheMongoRepository<Subscription> {

}
