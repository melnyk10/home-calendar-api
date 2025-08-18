package com.meln.event;

import com.meln.user.User;
import com.meln.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionService {
    private final SubscriptionRepo subscriptionRepo;
    private final UserService userService;

    public List<Subscription> getAllUserSubscriptions(String email) {
        User user = userService.getByEmail(email);
        if (user == null) {
            return Collections.emptyList();
        }
        return subscriptionRepo.find("userId", user.id).list();
    }
}
