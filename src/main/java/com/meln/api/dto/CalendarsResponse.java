package com.meln.api.dto;

import com.meln.app.calendar.model.Calendar;
import java.util.Collection;

public record CalendarsResponse(Collection<Calendar> calendars) {

}
