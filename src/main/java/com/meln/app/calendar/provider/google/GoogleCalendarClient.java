package com.meln.app.calendar.provider.google;

import com.meln.app.calendar.CalendarClient;
import com.meln.app.event.model.EventDto;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import javax.naming.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
//todo: Make the client stateless
public class GoogleCalendarClient implements CalendarClient<GoogleCalendarProperties> {

  @Override
  public Class<GoogleCalendarProperties> propertiesType() {
    return GoogleCalendarProperties.class;
  }

  @Override
  public void connect(GoogleCalendarProperties props) throws AuthenticationException {
    //todo: take google client and auth by props
  }

  @Override
  public String createEvent(EventDto event) {
    String eventId = UUID.randomUUID().toString();
    log.info("Creating a new event with id {}", eventId);
    return eventId;
  }

  @Override
  public void updateEvent(String eventId, EventDto event) {
    log.debug("Updating event with id {}", eventId);
  }

}
