package com.meln.api;

import com.meln.app.user.UserRepository;
import com.meln.app.user.model.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;

@Path(Endpoints.API_V1)
@AllArgsConstructor(onConstructor_ = @Inject)
public class UserResource {

  private final UserRepository userRepository;

  @GET
  @Path(Endpoints.User.ME)
  @Produces(MediaType.APPLICATION_JSON)
  public User me(@Context SecurityIdentity identity) {
    String email = identity.getPrincipal().getName();
    return userRepository.findByEmail(email);
  }

}
