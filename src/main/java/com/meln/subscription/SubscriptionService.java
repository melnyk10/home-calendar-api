package com.meln.subscription;

import com.meln.common.user.UserClient;
import com.meln.common.user.UserDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionService {
    private final SubscriptionRepo subscriptionRepo;
    private final UserClient userClient;

    public List<Subscription> getAllUserSubscriptions(String email) {
        UserDto user = userClient.getByEmail(email);
        return subscriptionRepo.find("userId", new ObjectId(user.getId())).list();
    }
}
