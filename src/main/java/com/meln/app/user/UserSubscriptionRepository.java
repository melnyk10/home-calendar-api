package com.meln.app.user;

import com.meln.app.user.model.UserSubscription;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class UserSubscriptionRepository implements PanacheRepository<UserSubscription> {

  List<UserSubscription> findAllByTargetIdIn(Collection<Long> targetIds) {
    return find("targetId in :targetIds", Parameters.with("targetIds", targetIds))
        .list();
  }
}
