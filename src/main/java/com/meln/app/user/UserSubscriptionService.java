package com.meln.app.user;

import com.meln.app.user.model.UserSubscription;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor
public class UserSubscriptionService {
private final UserSubscriptionRepository userSubscriptionRepository;

  public Map<Long, UserSubscription> listUserSubscriptionMap(Collection<Long> eventTargetIds) {
    var allByTargetIdIn = userSubscriptionRepository.findAllByTargetIdIn(eventTargetIds);
    return allByTargetIdIn.stream()
        .collect(Collectors.toMap(UserSubscription::getTargetId, Function.identity()));
  }
}
