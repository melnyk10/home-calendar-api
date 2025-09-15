package com.meln.app.common.error;

public interface ErrorMessage {

  interface Common {

    interface Code {

      String SOMETHING_WENT_WRONG = "SOMETHING_WENT_WRONG";
      String REQUEST_BODY_REQUIRED = "REQUEST_BODY_REQUIRED";
      String INVALID_REQUEST = "INVALID_REQUEST";
      String RATE_LIMITED = "RATE_LIMITED";
      String NETWORK_TIMEOUT = "NETWORK_TIMEOUT";
    }

    interface Message {

      String SOMETHING_WENT_WRONG = "Something went wrong! Please try again later.";
      String REQUEST_BODY_REQUIRED = "Payload must not be null";
      String NETWORK_TIMEOUT = "Network timeout. Please retry.";

      static String INVALID_REQUEST(String reason) {
        return "Invalid request: %s".formatted(reason);
      }
    }
  }

  interface Validation {

    interface Code {

      String INVALID_VALUE = "INVALID_VALUE";
    }
  }

  interface Auth {

    interface Code {

      String UNAUTHORIZED_OR_FORBIDDEN = "UNAUTHORIZED_OR_FORBIDDEN";
    }
  }

  interface Event {

    interface Code {

      String INVALID_EVENT_PROVIDER = "INVALID_EVENT_PROVIDER";
      String INVALID_EVENT_PROVIDER_PROPERTIES = "INVALID_EVENT_PROVIDER_PROPERTIES";
    }

    interface Message {

      String EVENT_PROVIDER_PROPERTIES_NOT_PROVIDED = "Event Provider properties not provided";

      static String NO_PROVIDER_BY_PROPS(String name) {
        return "No provider for: %s".formatted(name);
      }
    }
  }

  interface Hltv {

    interface Code {

      String HLTV_TEAMS_FETCH_FAILED = "HLTV_TEAMS_FETCH_FAILED";
      String HLTV_TEAMS_SAVE_FAILED = "HLTV_TEAMS_SAVE_FAILED";
      String HLTV_MATCHES_SAVE_FAILED = "HLTV_MATCHES_SAVE_FAILED";
      String HLTV_MATCHES_FETCH_FAILED = "HLTV_MATCHES_FETCH_FAILED";
    }

    interface Message {

      String HLTV_TEAMS_FETCH_FAILED = "Unable to fetch HLTV teams from source";
      String HLTV_MATCHES_FETCH_FAILED = "Unable to fetch HLTV matches from source";

      static String HLTV_TEAMS_SAVE_FAILED(int temaSize) {
        return String.format("Unable to save %d HLTV teams.", temaSize);
      }

      static String HLTV_MATCHES_SAVE_FAILED(int matchesSize) {
        return String.format("Unable to save %d HLTV matches.", matchesSize);
      }
    }
  }

  interface Calendar {

    interface Code {

      String INVALID_CALENDAR_PROVIDER = "INVALID_CALENDAR_PROVIDER";
      String INVALID_CALENDAR_PROVIDER_PROPERTIES = "INVALID_CALENDAR_PROVIDER_PROPERTIES";
      String CALENDAR_EVENT_SOURCE_ID_NOT_PROVIDED = "CALENDAR_EVENT_SOURCE_ID_NOT_PROVIDED";
      String CALENDAR_ID_NOT_PROVIDED = "CALENDAR_ID_NOT_PROVIDED";
      String EVENT_NOT_FOUND = "EVENT_NOT_FOUND";
    }

    interface Message {

      String CALENDAR_PROVIDER_PROPERTIES_NOT_PROVIDED = "Calendar Provider properties not provided";
      String CALENDAR_EVENT_SOURCE_ID_NOT_PROVIDED = "Calendar event source id not provided";
      String CALENDAR_ID_NOT_PROVIDED = "Calendar ID must not be blank";
      String START_AT_AND_END_AT_REQUIRED = "startAt and endAt are required for timed events";

      static String NO_PROVIDER_BY_PROPS(String name) {
        return "No provider for: %s".formatted(name);
      }

      static String EVENT_NOT_FOUND(String sourceId) {
        return "Event not found for id=%s (may be deleted or wrong calendar)".formatted(sourceId);
      }
    }
  }

  interface GoogleCalendar {

    interface Code {

      String GOOGLE_UNAUTHORIZED = "GOOGLE_UNAUTHORIZED";
    }

    interface Message {

      String RATE_LIMITED = "Rate limited by Google Calendar. Please retry with backoff.";

      static String UNAUTHORIZED_OR_FORBIDDEN(String reason) {
        return "Google authorization failed or forbidden: %s".formatted(reason);
      }

      static String GOOGLE_UNAUTHORIZED(String email) {
        return "Google account is not connected for user ".formatted(email);
      }

    }
  }

}
