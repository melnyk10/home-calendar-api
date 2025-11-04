package com.meln.api;

import com.meln.api.dto.CalendarProviderRequest;
import com.meln.app.user.CalendarProviderService;
import com.meln.app.user.model.CalendarProvider;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path(Endpoints.API_V1)
@AllArgsConstructor(onConstructor_ = @Inject)
public class CalendarProviderResource {

  private final CalendarProviderService calendarProviderService;

  @POST
  @Path(Endpoints.CalendarProvider.CALENDAR_PROVIDER)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response link(CalendarProviderRequest request) {
    var providerCalendarEntities = request.calendarProviders().stream()
        .map(it -> CalendarProvider.builder()
            .calendarId(it.calendarId())
            .providerId(it.providerId())
            .build())
        .toList();
    calendarProviderService.save(providerCalendarEntities);
    return Response.status(Response.Status.CREATED).build();
  }

}
