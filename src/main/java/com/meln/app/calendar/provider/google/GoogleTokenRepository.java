package com.meln.app.calendar.provider.google;

import com.meln.app.calendar.provider.google.model.GoogleToken;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class GoogleTokenRepository implements PanacheMongoRepository<GoogleToken> {

  public GoogleToken findByUserEmail(String email) {
    return find(GoogleToken.COL_USER_EMAIL, email).firstResult();
  }
}
