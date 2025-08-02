package com.meln.user;

import com.meln.common.EndPoints;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Path(EndPoints.API_V1 + EndPoints.User.USERS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserResource {
    private final UserService userService;

    @GET
    public List<User> getAllUsers() {
        return userService.getAll();
    }
}
