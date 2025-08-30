package com.meln.common.auth;

import com.meln.common.user.UserClient;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class KnownUserAugmentor implements SecurityIdentityAugmentor {

  private final UserClient userClient;

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

    String email = identity.getPrincipal().getName();
    if (email == null || email.isBlank()) {
      return Uni.createFrom().failure(new AuthenticationFailedException("Missing email claim"));
    }

    boolean exists = userClient.existsByEmail(email);
    if (!exists) {
      return Uni.createFrom().failure(new AuthenticationFailedException("Unknown user"));
    }
    return Uni.createFrom().item(identity);
  }

}
