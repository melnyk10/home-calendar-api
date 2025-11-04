package com.meln.app.calendar.provider.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.meln.app.calendar.CalendarConnectionRepository;
import com.meln.app.calendar.model.CalendarConnection;
import com.meln.app.calendar.model.CalendarProvider;
import com.meln.app.common.error.ErrorMessage;
import com.meln.app.common.error.ServerException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GoogleAuthService {

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  public static final NetHttpTransport HTTP = new NetHttpTransport();
  private static final List<String> SCOPES = List.of(
      CalendarScopes.CALENDAR_READONLY,
      CalendarScopes.CALENDAR_EVENTS
  );

  private final CalendarConnectionRepository repository;

  @ConfigProperty(name = "app.google.client-id")
  String clientId;

  @ConfigProperty(name = "app.google.client-secret")
  String clientSecret;

  @ConfigProperty(name = "app.google.auth-redirect-uri")
  String redirectUri;

  /**
   * Build the Google consent URL where user is redirected. Includes offline access + consent prompt
   * to ensure a refresh_token.
   */
  public String buildAuthUrl(String state) {
    var flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP,
        JSON_FACTORY,
        clientId,
        clientSecret,
        SCOPES
    )
        .setAccessType("offline")
        .build();

    return flow.newAuthorizationUrl()
        .setRedirectUri(redirectUri)      // must be absolute and whitelisted
        .setState(state)
        .setAccessType("offline")
        .set("prompt", "consent")
        .set("include_granted_scopes", "true")
        .build();
  }

  public GoogleTokenResponse requestToken(String code) throws Exception {
    return new GoogleAuthorizationCodeFlow.Builder(
        HTTP,
        JSON_FACTORY,
        clientId,
        clientSecret,
        SCOPES
    ).build()
        .newTokenRequest(code)
        .setRedirectUri(redirectUri)
        .execute();
  }

  Calendar calendarClient(CalendarConnection userToken) {
    if (userToken == null) {
      throw new ServerException(ErrorMessage.GoogleCalendar.Code.GOOGLE_UNAUTHORIZED,
          ErrorMessage.GoogleCalendar.Message.GOOGLE_UNAUTHORIZED);
    }
    return new Calendar.Builder(HTTP, JSON_FACTORY, requestInitializer(userToken))
        .setApplicationName("Home Calendar").build();
  }

  private HttpCredentialsAdapter requestInitializer(CalendarConnection token) {
    var accessExpiry = token.getExpiresAt() == null ? null : Date.from(token.getExpiresAt());

    var creds = UserCredentials.newBuilder()
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setRefreshToken(token.getRefreshToken()) // enables auto-refresh
        .setAccessToken(new AccessToken(token.getAccessToken(), accessExpiry))
        .build();

    return new HttpCredentialsAdapter(creds);
  }

  public Optional<CalendarConnection> findByUserEmail(String email) {
    return repository.findByUserEmail(email);
  }

  @Transactional
  public void saveOrUpdate(String email, GoogleTokenResponse tokens) {

    var expiresIn = tokens.getExpiresInSeconds() == null ? 3600L : tokens.getExpiresInSeconds();
    var googleToken = CalendarConnection.builder()
        .provider(CalendarProvider.GOOGLE)
        .email(email)
        .accessToken(tokens.getAccessToken())
        .refreshToken(tokens.getRefreshToken())
        .expiresAt(Instant.now().plusSeconds(expiresIn - 60))
        .scopes(SCOPES)
        .build();

    repository.persist(googleToken);
  }

  public void delete(CalendarConnection connection) {
    repository.delete(connection);
  }
}
