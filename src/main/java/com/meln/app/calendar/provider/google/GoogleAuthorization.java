package com.meln.app.calendar.provider.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GoogleAuthorization {

  public static final NetHttpTransport HTTP = new NetHttpTransport();
  public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final List<String> SCOPES = List.of(
      "https://www.googleapis.com/auth/calendar.events"
  );

  @ConfigProperty(name = "app.google.client-id")
  String clientId;

  @ConfigProperty(name = "app.google.client-secret")
  String clientSecret;

  @ConfigProperty(name = "app.google.redirect-uri")
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
        .setRedirectUri(redirectUri)
        .setState(state)
        .setAccessType("offline")
        .setApprovalPrompt("consent") // ensures refresh_token on first grant
        .build();
  }

  /**
   * Exchange an authorization code for tokens (access + refresh + id_token).
   */
  public GoogleTokenResponse exchangeCode(String code) throws Exception {
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

  public GoogleIdToken.Payload verifyIdToken(String idToken) throws Exception {
    var verifier = new GoogleIdTokenVerifier.Builder(HTTP, JSON_FACTORY)
        .setAudience(List.of(clientId))
        .build();

    GoogleIdToken token = verifier.verify(idToken);
    if (token == null) {
      throw new IllegalStateException("Invalid Google id_token");
    }
    return token.getPayload();
  }
}
