package com.meln.api;

import com.meln.app.user.model.UserInfo;
import com.meln.app.user.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;

@Path(Endpoints.API_V1)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@AllArgsConstructor(onConstructor_ = @Inject)
public class UserResource {

  private final UserService userService;

  @GET
  @Path(Endpoints.User.ME)
  public UserInfo me(@Context SecurityIdentity identity) {
    String email = identity.getPrincipal().getName();
    return userService.getByEmail(email);
  }

}
