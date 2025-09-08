package com.meln.app.user;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
class KnownUserAugmentor implements SecurityIdentityAugmentor {

  private final UserService userService;

  @Override
  public int priority() {
    return 10; // after default providers
  }

  @Override
  public Uni<SecurityIdentity> augment(SecurityIdentity identity,
      AuthenticationRequestContext context) {
    if (identity.isAnonymous()) {
      return Uni.createFrom().item(identity);
    }

    var email = identity.getPrincipal().getName();
    if (email == null || email.isBlank()) {
      return Uni.createFrom().failure(new AuthenticationFailedException("Missing email claim"));
    }

    boolean exists = userService.existsByEmail(email);
    if (!exists) {
      return Uni.createFrom().failure(new AuthenticationFailedException("Unknown user"));
    }
    return Uni.createFrom().item(identity);
  }

}
