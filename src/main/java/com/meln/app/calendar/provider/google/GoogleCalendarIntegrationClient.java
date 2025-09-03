package com.meln.app.calendar.provider.google;

import com.meln.app.calendar.CalendarIntegrationClient;
import com.meln.app.event.model.EventDto;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GoogleCalendarIntegrationClient implements
    CalendarIntegrationClient<GoogleCalendarProperties> {

  @Override
  public Class<GoogleCalendarProperties> propertiesType() {
    return GoogleCalendarProperties.class;
  }

  private Object prepareClient(GoogleCalendarProperties props) {
    //todo: prepare or auth and return client to make CRUD for Google Calendar
    return null;
  }

  @Override
  public String createEvent(GoogleCalendarProperties props, EventDto event) {
    Object client = prepareClient(props);
    String eventId = UUID.randomUUID().toString();
    log.info("Creating a new event with id {}", eventId);
    return eventId;
  }

  @Override
  public void updateEvent(GoogleCalendarProperties props, String eventId, EventDto event) {
    Object client = prepareClient(props);
    log.debug("Updating event with id {}", eventId);
  }

}
