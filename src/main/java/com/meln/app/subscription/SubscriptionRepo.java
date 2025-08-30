package com.meln.app.subscription;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubscriptionRepo implements PanacheMongoRepository<Subscription> {

}
