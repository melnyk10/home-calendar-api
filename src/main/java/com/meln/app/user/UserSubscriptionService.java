package com.meln.app.user;

import com.meln.app.user.model.UserSubscription;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor
public class UserSubscriptionService {

  private final UserSubscriptionRepository userSubscriptionRepository;

  public Map<Long, List<UserSubscription>> listUserSubscriptionMap(Collection<Long> targetIds) {
    return userSubscriptionRepository.findAllByTargetIdIn(targetIds).stream()
        .collect(Collectors.groupingBy(UserSubscription::getTargetId));
  }
}
