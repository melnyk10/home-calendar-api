package com.meln.app.calendar.provider.google;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.ExtendedProperties;
import com.google.api.services.calendar.model.EventDateTime;
import com.meln.app.calendar.CalendarClient;
import com.meln.app.event.model.EventPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.naming.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
class GoogleCalendarClient implements CalendarClient<GoogleCalendarProperties> {

  private final GoogleAuthService googleAuthService;

  @Override
  public Class<GoogleCalendarProperties> propertiesType() {
    return GoogleCalendarProperties.class;
  }

  @Override
  public CalendarConnection connect(GoogleCalendarProperties props) throws AuthenticationException {
    var calendarClient = googleAuthService.calendarClient(props.getUserEmail());
    var calendarId = Optional.ofNullable(props.getCalendarId()).orElse("primary");
    return new GoogleConnection(calendarClient, calendarId);
  }

  private record GoogleConnection(Calendar calendar, String calendarId) implements
      CalendarConnection {

    @Override
    public String createEvent(EventPayload event) {
      Event newEvent = buildEvent(event, new Event());
      Map<String, String> extraProps = Map.of("eventSourceId", event.getSourceId());
      updateExtraProps(newEvent, extraProps);

      Event newEventResponse;
      try {
        newEventResponse = calendar.events().insert(calendarId, newEvent).execute();
      } catch (IOException e) {
        log.error("Can't create event with url: {}", event.getUrl(), e);
        throw new RuntimeException(e);
      }

      return newEventResponse.getId();
    }

    @Override
    public void updateEvent(EventPayload event) {
      if (event.getCalendarEventSourceId() == null) {
        throw new IllegalArgumentException("event source id is null");
      }

      Event updateEvent = buildEvent(event, new Event());
      updateEvent.setId(event.getCalendarEventSourceId());

      try {
        calendar.events()
            .update(calendarId, event.getCalendarEventSourceId(), updateEvent)
            .execute();
      } catch (IOException e) {
        log.error("Error while updating event by id: {}", event.getId(), e);
        throw new RuntimeException(e);
      }
    }

    private Event buildEvent(EventPayload dto, Event event) {
      event.setSummary(dto.getTitle());
      event.setDescription(dto.getDetails());
      event.setLocation(dto.getUrl());

      EventDateTime googleStartDate;
      EventDateTime googleEndDate;
      if (dto.isAllDay()) {
        // Use date-only; Google interprets as all-day in calendar’googleStartDate TZ
        LocalDate startDate = dto.getStartAt() != null
            ? LocalDateTime.ofInstant(dto.getStartAt(), dto.getZone()).toLocalDate()
            : LocalDate.now(dto.getZone());
        LocalDate endDate = dto.getEndAt() != null
            ? LocalDateTime.ofInstant(dto.getEndAt(), dto.getZone()).toLocalDate()
            : startDate.plusDays(1); // Google expects end to be exclusive

        googleStartDate = new EventDateTime().setDate(
            new DateTime(true,
                Date.valueOf(startDate).getTime(), 0));
        googleEndDate = new EventDateTime()
            .setDate(new DateTime(true,
                Date.valueOf(endDate).getTime(), 0));
      } else {
        if (dto.getStartAt() == null || dto.getEndAt() == null) {
          throw new IllegalArgumentException("startAt and endAt are required for timed events");
        }
        googleStartDate = new EventDateTime()
            .setDateTime(new DateTime(Date.from(dto.getStartAt())))
            .setTimeZone(dto.getZone().getId());
        googleEndDate = new EventDateTime()
            .setDateTime(new DateTime(Date.from(dto.getEndAt())))
            .setTimeZone(dto.getZone().getId());
      }
      event.setStart(googleStartDate);
      event.setEnd(googleEndDate);

      return event;
    }

    private void updateExtraProps(Event event, Map<String, String> extraProps) {
      Map<String, String> extendedProperties = Optional.ofNullable(event)
          .map(Event::getExtendedProperties)
          .map(ExtendedProperties::getPrivate).orElse(new HashMap<>());

      extendedProperties.putAll(extraProps);

      var ext = new ExtendedProperties();
      ext.setPrivate(extendedProperties);

      Objects.requireNonNull(event).setExtendedProperties(ext);
    }
  }

}
