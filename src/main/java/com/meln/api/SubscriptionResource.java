package com.meln.api;

import com.meln.app.subscription.Subscription;
import com.meln.app.subscription.SubscriptionService;
import com.meln.app.user.UserService;
import com.meln.common.user.UserDto;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Path(Endpoints.API_V1)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionResource {

  private final SubscriptionService subscriptionService;
  private final UserService userService;

  @GET
  @Path(Endpoints.Subscription.SUBSCRIPTIONS)
  public List<Subscription> userSubscriptions(@Context SecurityIdentity identity) {
    String email = identity.getPrincipal().getName();
    UserDto user = userService.getByEmail(email);
    return subscriptionService.getAllUserSubscriptions(user.getId());
  }

}
