package com.meln.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.meln.api.Endpoints.GoogleCalendar;
import com.meln.app.calendar.provider.google.GoogleAuthService;
import com.meln.app.calendar.provider.google.InMemoryStateStore;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@Path(Endpoints.API_V1)
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GoogleOAuthResource {

  private final GoogleAuthService authService;
  private final GoogleAuthService tokenService;
  private final InMemoryStateStore stateStore;

  @ConfigProperty(name = "app.google.success-redirect")
  String successRedirect;

  @ConfigProperty(name = "app.google.failure-redirect")
  String failureRedirect;

  @GET
  @Path(GoogleCalendar.CONNECT)
  @Produces(MediaType.APPLICATION_JSON)
  public Response connect(@Context SecurityIdentity identity) {
    var email = identity.getPrincipal().getName();
    var state = stateStore.issue(email, Duration.ofMinutes(10));
    var consentUrl = authService.buildAuthUrl(state);
    return Response.seeOther(UriBuilder.fromUri(consentUrl).build()).build();
  }

  @GET
  @Path(Endpoints.GoogleCalendar.CALLBACK)
  @Produces(MediaType.APPLICATION_JSON)
  public Response callback(@QueryParam("code") String code, @QueryParam("state") String state) {
    if (code == null || code.isBlank()) {
      return redirectOrJsonError(400, "missing code");
    }
    if (state == null || state.isBlank()) {
      return redirectOrJsonError(400, "missing state");
    }

    var email = stateStore.findByState(state).orElse(null);
    try {
      GoogleTokenResponse tokenResp = authService.requestToken(code);
      tokenService.saveOrUpdate(email, tokenResp);
      return Response.seeOther(UriBuilder.fromUri(successRedirect).build()).build();
    } catch (Exception e) {
      log.error("Can't process google oauth callback. Error: {}", e.getMessage());
      return redirectOrJsonError(500, "oauth_error");
    }
  }

  @POST
  @Path(GoogleCalendar.DISCONNECT)
  public Response disconnect(@Context SecurityIdentity identity) {
    var email = identity.getPrincipal().getName();
    var token = tokenService.findByUserEmail(email);
    if (token != null) {
      tokenService.delete(token);
    }
    return Response.noContent().build();
  }

  private Response redirectOrJsonError(int status, String message) {
    if (failureRedirect != null && !failureRedirect.isBlank()) {
      var url = UriBuilder.fromUri(failureRedirect)
          .queryParam("reason", message)
          .build()
          .toString();
      return Response.seeOther(UriBuilder.fromUri(url).build()).build();
    }
    return Response.status(status).entity("{\"error\":\"" + message + "\"}").build();
  }
}
