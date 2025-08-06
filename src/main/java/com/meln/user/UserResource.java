package com.meln.user;

import com.meln.common.EndPoints;
import com.meln.common.error.Error;
import com.meln.common.error.ErrorMessage;
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
        if (email == null || email.isBlank()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        User user = userService.getByEmail(email);
        if (user == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(Error.from(
                            ErrorMessage.Auth.Code.USER_NOT_FOUND,
                            ErrorMessage.Auth.Message.USER_NOT_FOUND(email))
                    )
                    .build();
        }

        return Response.ok(UserMe.from(user)).build();
    }

}
