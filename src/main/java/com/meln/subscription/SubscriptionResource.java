package com.meln.subscription;

import com.meln.common.Endpoints;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path(Endpoints.API_V1)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SubscriptionResource {
    private final SubscriptionService subscriptionService;

    @GET
    @Path(Endpoints.Subscription.SUBSCRIPTIONS)
    public List<Subscription> userSubscriptions(@Context SecurityIdentity identity) {
        String email = identity.getPrincipal().getName();
        return subscriptionService.getAllUserSubscriptions(email);
    }

}
