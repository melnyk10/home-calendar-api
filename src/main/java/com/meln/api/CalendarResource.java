package com.meln.api;

import com.meln.api.Endpoints.Calendar;
import com.meln.api.dto.CalendarsResponse;
import com.meln.app.calendar.CalendarService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path(Endpoints.API_V1)
@AllArgsConstructor(onConstructor_ = @Inject)
public class CalendarResource {

  private final CalendarService calendarService;

  @GET
  @Path(Calendar.CALENDARS)
  @Produces(MediaType.APPLICATION_JSON)
  public CalendarsResponse list(@Context SecurityIdentity identity) {
    var email = identity.getPrincipal().getName();
    var calendars = calendarService.list(email);
    return new CalendarsResponse(calendars);
  }

  @DELETE
  @Path(Calendar.CALENDARS)
  public Response delete(@Context SecurityIdentity identity,
      @QueryParam("ids") List<Integer> calendarIds) {
    var email = identity.getPrincipal().getName();
    calendarService.delete(email, calendarIds);
    return Response.status(Status.OK).build();
  }

  @GET
  @Path(Calendar.SYNC)
  public Response sync(@Context SecurityIdentity identity) {
    var email = identity.getPrincipal().getName();
    calendarService.sync(email);
    return Response.status(Status.OK).build();
  }

}
