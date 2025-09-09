package com.meln.api;

import com.meln.app.subscription.SubscriptionRepository;
import com.meln.app.subscription.model.Subscription;
import com.meln.app.user.UserRepository;
import com.meln.app.user.model.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.AllArgsConstructor;

@Path(Endpoints.API_V1)
@AllArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionResource {

  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;

  @GET
  @Path(Endpoints.Subscription.SUBSCRIPTIONS)
  @Produces(MediaType.APPLICATION_JSON)
  public GetUserSubscriptions userSubscriptions(@Context SecurityIdentity identity) {
    String email = identity.getPrincipal().getName();
    User user = userRepository.findByEmail(email);
    return new GetUserSubscriptions(
        subscriptionRepository.listUserSubscriptions(user.getId().toString()));
  }

  public record GetUserSubscriptions(List<Subscription> subscriptions) {

  }

}
