package com.meln.app.calendar.provider.google;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.meln.app.calendar.CalendarClient;
import com.meln.app.calendar.CalendarPayload;
import com.meln.app.calendar.model.CalendarConnection;
import com.meln.app.calendar.model.CalendarProvider;
import com.meln.app.common.error.CustomException.CustomAuthException;
import com.meln.app.common.error.CustomException.CustomBadRequestException;
import com.meln.app.common.error.CustomException.CustomRetryableException;
import com.meln.app.common.error.CustomException.CustomUnprocessableEntityException;
import com.meln.app.common.error.ErrorMessage;
import com.meln.app.common.error.ServerException;
import com.meln.app.event.model.EventPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
class GoogleCalendarClient implements CalendarClient {

  private final GoogleAuthService googleAuthService;

  @Override
  public CalendarProvider type() {
    return CalendarProvider.GOOGLE;
  }

  @Override
  public CalendarEventClient auth(CalendarConnection connection) {
    var calendarClient = googleAuthService.calendarClient(connection);
    return new Google(calendarClient);
  }

  private record Google(Calendar calendar) implements CalendarEventClient {

    @Override
    public List<CalendarPayload> listCalendars() {
      List<CalendarPayload> result = new ArrayList<>();

      String pageToken = null;
      do {
        CalendarList calendarsResponse;
        try {
          calendarsResponse = calendar.calendarList()
              .list()
              .setPageToken(pageToken)
              .execute();
        } catch (IOException e) {
          log.error("Error listing calendars", e);
          throw new ServerException("CANNOT_LIST_CALENDARS", "Can't list google calendars");
        }

        var mainEmail = findMainAccount(calendarsResponse.getItems());
        if (mainEmail.isEmpty()) {
          return List.of();
        }

        var calendars = mapCalendars(mainEmail.get(), calendarsResponse.getItems());
        result.addAll(calendars);
        pageToken = calendarsResponse.getNextPageToken();
      } while (pageToken != null);

      return result;
    }

    private Optional<String> findMainAccount(Collection<CalendarListEntry> calendars) {
      return calendars.stream()
          .filter(CalendarListEntry::isPrimary)
          .map(CalendarListEntry::getId)
          .findFirst();
    }

    private List<CalendarPayload> mapCalendars(String email,
        Collection<CalendarListEntry> calendars) {
      return calendars.stream()
          .filter(it -> !it.isPrimary())
          .filter(CalendarListEntry::isSelected)
          .map(it -> CalendarPayload.builder()
              .id(it.getId())
              .name(it.getSummary())
              .email(email)
              .provider(CalendarProvider.GOOGLE)
              .build())
          .toList();
    }

    @Override
    public String createEvent(String calendarId, EventPayload event) {
      Event response = withGoogleExceptionHandling(() -> {
        var newEvent = buildEvent(event, new Event());

        try {
          var eventCreationResponse = calendar.events().insert(calendarId, newEvent).execute();
          log.debug("Created event: id={}, etag={}", newEvent.getId(), newEvent.getEtag());
          return eventCreationResponse;
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }, calendarId, event);

      return response.getId();
    }

    @Override
    public void updateEvent(String calendarId, EventPayload event) {
      withGoogleExceptionHandling(() -> {
        var updateEvent = buildEvent(event, new Event());
        updateEvent.setId(event.calendarEventSourceId());
        Event updated;
        try {
          updated = calendar.events()
              .update(calendarId, event.calendarEventSourceId(), updateEvent)
              .execute();
          log.debug("Updated event; id={}, etag={}", updated.getId(), updated.getEtag());
          return updated;
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }, calendarId, event);
    }

    private <T> T withGoogleExceptionHandling(Supplier<T> call, String calendarId,
        EventPayload event) {
      if (event == null) {
        throw new ServerException(
            ErrorMessage.Common.Code.REQUEST_BODY_REQUIRED,
            ErrorMessage.Common.Message.REQUEST_BODY_REQUIRED);
      }
      if (event.calendarEventSourceId() == null || event.calendarEventSourceId().isBlank()) {
        throw new ServerException(
            ErrorMessage.Calendar.Code.CALENDAR_EVENT_SOURCE_ID_NOT_PROVIDED,
            ErrorMessage.Calendar.Message.CALENDAR_EVENT_SOURCE_ID_NOT_PROVIDED);
      }
      if (calendarId == null || calendarId.isBlank()) {
        throw new ServerException(
            ErrorMessage.Calendar.Code.CALENDAR_ID_NOT_PROVIDED,
            ErrorMessage.Calendar.Message.CALENDAR_ID_NOT_PROVIDED);
      }

      try {
        return call.get();
      } catch (Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof GoogleJsonResponseException googleEx) {
          int status = googleEx.getStatusCode();
          String reason = googleEx.getDetails() != null ?
              googleEx.getDetails().getMessage() :
              googleEx.getMessage();

          switch (status) {
            case 400 -> throw new ServerException(
                ErrorMessage.Common.Code.INVALID_REQUEST,
                ErrorMessage.Common.Message.INVALID_REQUEST(reason));
            case 401, 403 -> throw new CustomAuthException(
                ErrorMessage.Auth.Code.UNAUTHORIZED_OR_FORBIDDEN,
                ErrorMessage.GoogleCalendar.Message.UNAUTHORIZED_OR_FORBIDDEN(reason));
            case 404 -> throw new CustomBadRequestException(
                ErrorMessage.Calendar.Code.EVENT_NOT_FOUND,
                ErrorMessage.Calendar.Message.EVENT_NOT_FOUND(event.sourceId()));
            case 429 -> throw new CustomRetryableException(
                ErrorMessage.Common.Code.RATE_LIMITED,
                ErrorMessage.GoogleCalendar.Message.RATE_LIMITED);
            default -> throw new ServerException(
                ErrorMessage.Common.Code.SOMETHING_WENT_WRONG,
                ErrorMessage.Common.Message.SOMETHING_WENT_WRONG);
          }
        } else if (cause instanceof java.net.SocketTimeoutException
            || cause instanceof java.net.ConnectException) {
          throw new CustomRetryableException(ErrorMessage.Common.Code.NETWORK_TIMEOUT,
              ErrorMessage.Common.Message.NETWORK_TIMEOUT);
        } else {
          throw new ServerException(
              ErrorMessage.Common.Code.SOMETHING_WENT_WRONG,
              ErrorMessage.Common.Message.SOMETHING_WENT_WRONG);
        }
      }
    }

    private Event buildEvent(EventPayload dto, Event event) {
      event.setSummary(dto.name());
      event.setDescription(dto.name());
      event.setLocation(dto.url());

      EventDateTime googleStartDate;
      EventDateTime googleEndDate;
      if (dto.isAllDay()) {
        // Use date-only; Google interprets as all-day in calendar’googleStartDate TZ
        var startDate = dto.startAt() != null
            ? LocalDateTime.ofInstant(dto.startAt(), dto.zone()).toLocalDate()
            : LocalDate.now(dto.zone());
        var endDate = dto.endAt() != null
            ? LocalDateTime.ofInstant(dto.endAt(), dto.zone()).toLocalDate()
            : startDate.plusDays(1); // Google expects end to be exclusive

        googleStartDate = new EventDateTime().setDate(
            new DateTime(true,
                Date.valueOf(startDate).getTime(), 0));
        googleEndDate = new EventDateTime()
            .setDate(new DateTime(true,
                Date.valueOf(endDate).getTime(), 0));
      } else {
        if (dto.startAt() == null || dto.endAt() == null) {
          throw new CustomUnprocessableEntityException(ErrorMessage.Validation.Code.INVALID_VALUE,
              ErrorMessage.Calendar.Message.START_AT_AND_END_AT_REQUIRED);
        }
        googleStartDate = new EventDateTime()
            .setDateTime(new DateTime(Date.from(dto.startAt())))
            .setTimeZone(dto.zone().getId());
        googleEndDate = new EventDateTime()
            .setDateTime(new DateTime(Date.from(dto.endAt())))
            .setTimeZone(dto.zone().getId());
      }
      event.setStart(googleStartDate);
      event.setEnd(googleEndDate);

      return event;
    }

  }

}
