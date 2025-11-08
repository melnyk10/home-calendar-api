package com.meln.api.dto;

import java.util.List;

public record CalendarProviderRequest(List<CalendarProvider> calendarProviders) {

  public record CalendarProvider(Integer calendarId, Integer providerId) {

  }

}
