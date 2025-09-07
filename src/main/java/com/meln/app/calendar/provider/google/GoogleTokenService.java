package com.meln.app.calendar.provider.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.meln.app.calendar.provider.google.model.GoogleToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Date;
import java.time.Instant;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GoogleTokenService {

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  public static final NetHttpTransport HTTP = new NetHttpTransport();

  private final GoogleTokenRepository repository;

  @ConfigProperty(name = "app.google.client-id")
  String clientId;

  @ConfigProperty(name = "app.google.client-secret")
  String clientSecret;

  public GoogleToken saveOrUpdate(String userId,
      GoogleIdToken.Payload payload,
      GoogleTokenResponse tokens) {

    var expiresIn = tokens.getExpiresInSeconds() == null ? 3600L : tokens.getExpiresInSeconds();
    GoogleToken googleToken = GoogleToken.builder()
        .userId(userId)
        .googleSub((String) payload.get("sub"))
        .email((String) payload.get("email"))
        .accessToken(tokens.getAccessToken())
        .refreshToken(tokens.getRefreshToken())
        .expiresAt(Instant.now().plusSeconds(expiresIn - 60))
        .scopes(Set.of(CalendarScopes.CALENDAR_EVENTS))
        .build();

    repository.persist(googleToken);
    return googleToken;
  }

  public Calendar calendarClient(String userId) {
    GoogleToken userToken = findByUser(userId);
    if (userToken == null) {
      //todo: redirect user to connect/auth google
      throw new IllegalStateException("Google account is not connected for user " + userId);
    }
    return new Calendar.Builder(HTTP, JSON_FACTORY, requestInitializer(userToken))
        .setApplicationName("Home Calendar").build();
  }

  private HttpCredentialsAdapter requestInitializer(GoogleToken token) {
    var accessExpiry = token.getExpiresAt() == null ? null : Date.from(token.getExpiresAt());

    var creds = UserCredentials.newBuilder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRefreshToken(token.getRefreshToken()) // enables auto-refresh
        .setAccessToken(new AccessToken(token.getAccessToken(), accessExpiry))
        .build();

    return new HttpCredentialsAdapter(creds);
  }

  public GoogleToken findByUser(String userId) {
    return repository.findByUserId(userId);
  }
}
