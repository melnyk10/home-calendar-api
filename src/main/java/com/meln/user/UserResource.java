package com.meln.user;

import com.meln.common.EndPoints;
import com.meln.common.error.Error;
import com.meln.common.error.ErrorMessage;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Authenticated
@Path(EndPoints.API_V1 + EndPoints.User.USERS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserResource {
    private final JsonWebToken jwt;
    private final UserService userService;

    @GET
    @Authenticated
    @Path(EndPoints.User.ME)
    public Response me() {
        String email = jwt.getClaim("email");
        if (email == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        User user = userService.getByEmail(email);
        if (user == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(Error.builder()
                            .code(ErrorMessage.Auth.Code.USER_NOT_FOUND)
                            .message(ErrorMessage.Auth.Message.USER_NOT_FOUND(email))
                            .build())
                    .build();
        }

        return Response.ok(user).build();
    }

    @GET
    public List<User> getAllUsers() {
        return userService.getAll();
    }
}
