package com.meln.user;

import com.meln.common.EndPoints;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path(EndPoints.API_V1)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserResource {
    private final UserService userService;

    @GET
    @Path(EndPoints.User.ME)
    public Response me(@Context SecurityIdentity identity) {
        String email = identity.getPrincipal().getName();
        return Response.ok(userService.me(email)).build();
    }

}
