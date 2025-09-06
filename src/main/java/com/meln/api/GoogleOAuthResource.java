package com.meln.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.meln.api.Endpoints.GoogleCalendar;
import com.meln.app.calendar.provider.google.GoogleAuthorization;
import com.meln.app.calendar.provider.google.GoogleTokenService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path(Endpoints.API_V1)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GoogleOAuthResource {

  private final GoogleAuthorization auth;
  private final GoogleTokenService tokenService;
  private final SecurityIdentity identity;

  @ConfigProperty(name = "app.google.connect.success-redirect", defaultValue = "/connected/google")
  String successRedirect;

  @ConfigProperty(name = "app.google.connect.failure-redirect", defaultValue = "/connect/google-failed")
  String failureRedirect;

  @GET
  @Path(GoogleCalendar.CONNECT)
  @Authenticated
  public Response connect(@QueryParam("returnTo") @DefaultValue("") String returnTo) {
    // Optionally remember returnTo in your own session (not shown here)
    String consentUrl = auth.buildAuthUrl(null); // or "", ignores state
    return Response.seeOther(UriBuilder.fromUri(consentUrl).build()).build();
  }

  @GET
  @Path(Endpoints.GoogleCalendar.CALLBACK)
  public Response callback(@QueryParam("code") String code) {
    if (code == null || code.isBlank()) {
      return redirectOrJsonError(400, "missing code");
    }
    if (identity == null || identity.isAnonymous()) {
      return redirectOrJsonError(401, "no app session for callback");
    }

    String userId = identity.getPrincipal().getName();
    try {
      GoogleTokenResponse tokenResp = auth.exchangeCode(code);
      GoogleIdToken.Payload idPayload = auth.verifyIdToken(tokenResp.getIdToken());
      tokenService.saveOrUpdate(userId, idPayload, tokenResp);
      return Response.seeOther(UriBuilder.fromUri(successRedirect).build()).build();
    } catch (Exception e) {
      return redirectOrJsonError(500, "oauth_error");
    }
  }

  @POST
  @Path(GoogleCalendar.DISCONNECT)
  @Authenticated
  public Response disconnect() {
    String userId = identity.getPrincipal().getName();
    var token = tokenService.findByUser(userId);
    if (token != null) {
      token.delete();
    }
    return Response.noContent().build();
  }

  private Response redirectOrJsonError(int status, String message) {
    if (failureRedirect != null && !failureRedirect.isBlank()) {
      String url = UriBuilder.fromUri(failureRedirect)
          .queryParam("reason", message)
          .build()
          .toString();
      return Response.seeOther(UriBuilder.fromUri(url).build()).build();
    }
    return Response.status(status).entity("{\"error\":\"" + message + "\"}").build();
  }
}
